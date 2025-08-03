/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import org.jsoup.parser.CharacterReader;
import org.jsoup.parser.Token;
import org.jsoup.parser.Tokeniser;

enum TokeniserState {
    Data{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case '&': {
                    t.advanceTransition(CharacterReferenceInData);
                    break;
                }
                case '<': {
                    t.advanceTransition(TagOpen);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.emit(r.consume());
                    break;
                }
                case '\uffff': {
                    t.emit(new Token.EOF());
                    break;
                }
                default: {
                    String data = r.consumeData();
                    t.emit(data);
                }
            }
        }
    }
    ,
    CharacterReferenceInData{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readCharRef(t, 2.Data);
        }
    }
    ,
    Rcdata{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case '&': {
                    t.advanceTransition(CharacterReferenceInRcdata);
                    break;
                }
                case '<': {
                    t.advanceTransition(RcdataLessthanSign);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    r.advance();
                    t.emit('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.emit(new Token.EOF());
                    break;
                }
                default: {
                    String data = r.consumeData();
                    t.emit(data);
                }
            }
        }
    }
    ,
    CharacterReferenceInRcdata{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readCharRef(t, 4.Rcdata);
        }
    }
    ,
    Rawtext{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readRawData(t, r, (TokeniserState)this, 5.RawtextLessthanSign);
        }
    }
    ,
    ScriptData{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readRawData(t, r, (TokeniserState)this, 6.ScriptDataLessthanSign);
        }
    }
    ,
    PLAINTEXT{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case '\u0000': {
                    t.error(this);
                    r.advance();
                    t.emit('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.emit(new Token.EOF());
                    break;
                }
                default: {
                    String data = r.consumeTo('\u0000');
                    t.emit(data);
                }
            }
        }
    }
    ,
    TagOpen{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case '!': {
                    t.advanceTransition(MarkupDeclarationOpen);
                    break;
                }
                case '/': {
                    t.advanceTransition(EndTagOpen);
                    break;
                }
                case '?': {
                    t.createBogusCommentPending();
                    t.advanceTransition(BogusComment);
                    break;
                }
                default: {
                    if (r.matchesLetter()) {
                        t.createTagPending(true);
                        t.transition(TagName);
                        break;
                    }
                    t.error(this);
                    t.emit('<');
                    t.transition(Data);
                }
            }
        }
    }
    ,
    EndTagOpen{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.emit("</");
                t.transition(Data);
            } else if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(TagName);
            } else if (r.matches('>')) {
                t.error(this);
                t.advanceTransition(Data);
            } else {
                t.error(this);
                t.createBogusCommentPending();
                t.advanceTransition(BogusComment);
            }
        }
    }
    ,
    TagName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            String tagName = r.consumeTagName();
            t.tagPending.appendTagName(tagName);
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(BeforeAttributeName);
                    break;
                }
                case '/': {
                    t.transition(SelfClosingStartTag);
                    break;
                }
                case '<': {
                    r.unconsume();
                    t.error(this);
                }
                case '>': {
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '\u0000': {
                    t.tagPending.appendTagName(replacementStr);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    t.tagPending.appendTagName(c);
                }
            }
        }
    }
    ,
    RcdataLessthanSign{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(RCDATAEndTagOpen);
            } else if (r.matchesLetter() && t.appropriateEndTagName() != null && !r.containsIgnoreCase("</" + t.appropriateEndTagName())) {
                t.tagPending = t.createTagPending(false).name(t.appropriateEndTagName());
                t.emitTagPending();
                r.unconsume();
                t.transition(Data);
            } else {
                t.emit("<");
                t.transition(Rcdata);
            }
        }
    }
    ,
    RCDATAEndTagOpen{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(r.current());
                t.dataBuffer.append(r.current());
                t.advanceTransition(RCDATAEndTagName);
            } else {
                t.emit("</");
                t.transition(Rcdata);
            }
        }
    }
    ,
    RCDATAEndTagName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.tagPending.appendTagName(name);
                t.dataBuffer.append(name);
                return;
            }
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    if (t.isAppropriateEndTagToken()) {
                        t.transition(BeforeAttributeName);
                        break;
                    }
                    this.anythingElse(t, r);
                    break;
                }
                case '/': {
                    if (t.isAppropriateEndTagToken()) {
                        t.transition(SelfClosingStartTag);
                        break;
                    }
                    this.anythingElse(t, r);
                    break;
                }
                case '>': {
                    if (t.isAppropriateEndTagToken()) {
                        t.emitTagPending();
                        t.transition(Data);
                        break;
                    }
                    this.anythingElse(t, r);
                    break;
                }
                default: {
                    this.anythingElse(t, r);
                }
            }
        }

        private void anythingElse(Tokeniser t, CharacterReader r) {
            t.emit("</" + t.dataBuffer.toString());
            r.unconsume();
            t.transition(Rcdata);
        }
    }
    ,
    RawtextLessthanSign{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(RawtextEndTagOpen);
            } else {
                t.emit('<');
                t.transition(Rawtext);
            }
        }
    }
    ,
    RawtextEndTagOpen{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readEndTag(t, r, 15.RawtextEndTagName, 15.Rawtext);
        }
    }
    ,
    RawtextEndTagName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, 16.Rawtext);
        }
    }
    ,
    ScriptDataLessthanSign{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '/': {
                    t.createTempBuffer();
                    t.transition(ScriptDataEndTagOpen);
                    break;
                }
                case '!': {
                    t.emit("<!");
                    t.transition(ScriptDataEscapeStart);
                    break;
                }
                case '\uffff': {
                    t.emit("<");
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    t.emit("<");
                    r.unconsume();
                    t.transition(ScriptData);
                }
            }
        }
    }
    ,
    ScriptDataEndTagOpen{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.readEndTag(t, r, 18.ScriptDataEndTagName, 18.ScriptData);
        }
    }
    ,
    ScriptDataEndTagName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, 19.ScriptData);
        }
    }
    ,
    ScriptDataEscapeStart{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('-')) {
                t.emit('-');
                t.advanceTransition(ScriptDataEscapeStartDash);
            } else {
                t.transition(ScriptData);
            }
        }
    }
    ,
    ScriptDataEscapeStartDash{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('-')) {
                t.emit('-');
                t.advanceTransition(ScriptDataEscapedDashDash);
            } else {
                t.transition(ScriptData);
            }
        }
    }
    ,
    ScriptDataEscaped{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }
            switch (r.current()) {
                case '-': {
                    t.emit('-');
                    t.advanceTransition(ScriptDataEscapedDash);
                    break;
                }
                case '<': {
                    t.advanceTransition(ScriptDataEscapedLessthanSign);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    r.advance();
                    t.emit('\ufffd');
                    break;
                }
                default: {
                    String data = r.consumeToAny('-', '<', '\u0000');
                    t.emit(data);
                }
            }
        }
    }
    ,
    ScriptDataEscapedDash{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }
            char c = r.consume();
            switch (c) {
                case '-': {
                    t.emit(c);
                    t.transition(ScriptDataEscapedDashDash);
                    break;
                }
                case '<': {
                    t.transition(ScriptDataEscapedLessthanSign);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.emit('\ufffd');
                    t.transition(ScriptDataEscaped);
                    break;
                }
                default: {
                    t.emit(c);
                    t.transition(ScriptDataEscaped);
                }
            }
        }
    }
    ,
    ScriptDataEscapedDashDash{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }
            char c = r.consume();
            switch (c) {
                case '-': {
                    t.emit(c);
                    break;
                }
                case '<': {
                    t.transition(ScriptDataEscapedLessthanSign);
                    break;
                }
                case '>': {
                    t.emit(c);
                    t.transition(ScriptData);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.emit('\ufffd');
                    t.transition(ScriptDataEscaped);
                    break;
                }
                default: {
                    t.emit(c);
                    t.transition(ScriptDataEscaped);
                }
            }
        }
    }
    ,
    ScriptDataEscapedLessthanSign{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTempBuffer();
                t.dataBuffer.append(r.current());
                t.emit("<" + r.current());
                t.advanceTransition(ScriptDataDoubleEscapeStart);
            } else if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(ScriptDataEscapedEndTagOpen);
            } else {
                t.emit('<');
                t.transition(ScriptDataEscaped);
            }
        }
    }
    ,
    ScriptDataEscapedEndTagOpen{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(r.current());
                t.dataBuffer.append(r.current());
                t.advanceTransition(ScriptDataEscapedEndTagName);
            } else {
                t.emit("</");
                t.transition(ScriptDataEscaped);
            }
        }
    }
    ,
    ScriptDataEscapedEndTagName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataEndTag(t, r, 27.ScriptDataEscaped);
        }
    }
    ,
    ScriptDataDoubleEscapeStart{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataDoubleEscapeTag(t, r, 28.ScriptDataDoubleEscaped, 28.ScriptDataEscaped);
        }
    }
    ,
    ScriptDataDoubleEscaped{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.current();
            switch (c) {
                case '-': {
                    t.emit(c);
                    t.advanceTransition(ScriptDataDoubleEscapedDash);
                    break;
                }
                case '<': {
                    t.emit(c);
                    t.advanceTransition(ScriptDataDoubleEscapedLessthanSign);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    r.advance();
                    t.emit('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    String data = r.consumeToAny('-', '<', '\u0000');
                    t.emit(data);
                }
            }
        }
    }
    ,
    ScriptDataDoubleEscapedDash{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-': {
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedDashDash);
                    break;
                }
                case '<': {
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedLessthanSign);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.emit('\ufffd');
                    t.transition(ScriptDataDoubleEscaped);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscaped);
                }
            }
        }
    }
    ,
    ScriptDataDoubleEscapedDashDash{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-': {
                    t.emit(c);
                    break;
                }
                case '<': {
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedLessthanSign);
                    break;
                }
                case '>': {
                    t.emit(c);
                    t.transition(ScriptData);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.emit('\ufffd');
                    t.transition(ScriptDataDoubleEscaped);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscaped);
                }
            }
        }
    }
    ,
    ScriptDataDoubleEscapedLessthanSign{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.emit('/');
                t.createTempBuffer();
                t.advanceTransition(ScriptDataDoubleEscapeEnd);
            } else {
                t.transition(ScriptDataDoubleEscaped);
            }
        }
    }
    ,
    ScriptDataDoubleEscapeEnd{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            TokeniserState.handleDataDoubleEscapeTag(t, r, 33.ScriptDataEscaped, 33.ScriptDataDoubleEscaped);
        }
    }
    ,
    BeforeAttributeName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    break;
                }
                case '/': {
                    t.transition(SelfClosingStartTag);
                    break;
                }
                case '<': {
                    r.unconsume();
                    t.error(this);
                }
                case '>': {
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '\u0000': {
                    r.unconsume();
                    t.error(this);
                    t.tagPending.newAttribute();
                    t.transition(AttributeName);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                case '\"': 
                case '\'': 
                case '=': {
                    t.error(this);
                    t.tagPending.newAttribute();
                    t.tagPending.appendAttributeName(c);
                    t.transition(AttributeName);
                    break;
                }
                default: {
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
                }
            }
        }
    }
    ,
    AttributeName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            String name = r.consumeToAnySorted(attributeNameCharsSorted);
            t.tagPending.appendAttributeName(name);
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(AfterAttributeName);
                    break;
                }
                case '/': {
                    t.transition(SelfClosingStartTag);
                    break;
                }
                case '=': {
                    t.transition(BeforeAttributeValue);
                    break;
                }
                case '>': {
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.tagPending.appendAttributeName('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                case '\"': 
                case '\'': 
                case '<': {
                    t.error(this);
                    t.tagPending.appendAttributeName(c);
                    break;
                }
                default: {
                    t.tagPending.appendAttributeName(c);
                }
            }
        }
    }
    ,
    AfterAttributeName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    break;
                }
                case '/': {
                    t.transition(SelfClosingStartTag);
                    break;
                }
                case '=': {
                    t.transition(BeforeAttributeValue);
                    break;
                }
                case '>': {
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.tagPending.appendAttributeName('\ufffd');
                    t.transition(AttributeName);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                case '\"': 
                case '\'': 
                case '<': {
                    t.error(this);
                    t.tagPending.newAttribute();
                    t.tagPending.appendAttributeName(c);
                    t.transition(AttributeName);
                    break;
                }
                default: {
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
                }
            }
        }
    }
    ,
    BeforeAttributeValue{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    break;
                }
                case '\"': {
                    t.transition(AttributeValue_doubleQuoted);
                    break;
                }
                case '&': {
                    r.unconsume();
                    t.transition(AttributeValue_unquoted);
                    break;
                }
                case '\'': {
                    t.transition(AttributeValue_singleQuoted);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.tagPending.appendAttributeValue('\ufffd');
                    t.transition(AttributeValue_unquoted);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '>': {
                    t.error(this);
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '<': 
                case '=': 
                case '`': {
                    t.error(this);
                    t.tagPending.appendAttributeValue(c);
                    t.transition(AttributeValue_unquoted);
                    break;
                }
                default: {
                    r.unconsume();
                    t.transition(AttributeValue_unquoted);
                }
            }
        }
    }
    ,
    AttributeValue_doubleQuoted{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAnySorted(attributeDoubleValueCharsSorted);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            } else {
                t.tagPending.setEmptyAttributeValue();
            }
            char c = r.consume();
            switch (c) {
                case '\"': {
                    t.transition(AfterAttributeValue_quoted);
                    break;
                }
                case '&': {
                    int[] ref = t.consumeCharacterReference(Character.valueOf('\"'), true);
                    if (ref != null) {
                        t.tagPending.appendAttributeValue(ref);
                        break;
                    }
                    t.tagPending.appendAttributeValue('&');
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.tagPending.appendAttributeValue('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    t.tagPending.appendAttributeValue(c);
                }
            }
        }
    }
    ,
    AttributeValue_singleQuoted{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAnySorted(attributeSingleValueCharsSorted);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            } else {
                t.tagPending.setEmptyAttributeValue();
            }
            char c = r.consume();
            switch (c) {
                case '\'': {
                    t.transition(AfterAttributeValue_quoted);
                    break;
                }
                case '&': {
                    int[] ref = t.consumeCharacterReference(Character.valueOf('\''), true);
                    if (ref != null) {
                        t.tagPending.appendAttributeValue(ref);
                        break;
                    }
                    t.tagPending.appendAttributeValue('&');
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.tagPending.appendAttributeValue('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    t.tagPending.appendAttributeValue(c);
                }
            }
        }
    }
    ,
    AttributeValue_unquoted{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAnySorted(attributeValueUnquoted);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            }
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(BeforeAttributeName);
                    break;
                }
                case '&': {
                    int[] ref = t.consumeCharacterReference(Character.valueOf('>'), true);
                    if (ref != null) {
                        t.tagPending.appendAttributeValue(ref);
                        break;
                    }
                    t.tagPending.appendAttributeValue('&');
                    break;
                }
                case '>': {
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.tagPending.appendAttributeValue('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                case '\"': 
                case '\'': 
                case '<': 
                case '=': 
                case '`': {
                    t.error(this);
                    t.tagPending.appendAttributeValue(c);
                    break;
                }
                default: {
                    t.tagPending.appendAttributeValue(c);
                }
            }
        }
    }
    ,
    AfterAttributeValue_quoted{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(BeforeAttributeName);
                    break;
                }
                case '/': {
                    t.transition(SelfClosingStartTag);
                    break;
                }
                case '>': {
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    r.unconsume();
                    t.error(this);
                    t.transition(BeforeAttributeName);
                }
            }
        }
    }
    ,
    SelfClosingStartTag{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '>': {
                    t.tagPending.selfClosing = true;
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.transition(Data);
                    break;
                }
                default: {
                    r.unconsume();
                    t.error(this);
                    t.transition(BeforeAttributeName);
                }
            }
        }
    }
    ,
    BogusComment{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            r.unconsume();
            t.commentPending.append(r.consumeTo('>'));
            char next = r.consume();
            if (next == '>' || next == '\uffff') {
                t.emitCommentPending();
                t.transition(Data);
            }
        }
    }
    ,
    MarkupDeclarationOpen{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchConsume("--")) {
                t.createCommentPending();
                t.transition(CommentStart);
            } else if (r.matchConsumeIgnoreCase("DOCTYPE")) {
                t.transition(Doctype);
            } else if (r.matchConsume("[CDATA[")) {
                t.createTempBuffer();
                t.transition(CdataSection);
            } else {
                t.error(this);
                t.createBogusCommentPending();
                t.advanceTransition(BogusComment);
            }
        }
    }
    ,
    CommentStart{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-': {
                    t.transition(CommentStartDash);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.commentPending.append('\ufffd');
                    t.transition(Comment);
                    break;
                }
                case '>': {
                    t.error(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                default: {
                    r.unconsume();
                    t.transition(Comment);
                }
            }
        }
    }
    ,
    CommentStartDash{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-': {
                    t.transition(CommentStartDash);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.commentPending.append('\ufffd');
                    t.transition(Comment);
                    break;
                }
                case '>': {
                    t.error(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.commentPending.append(c);
                    t.transition(Comment);
                }
            }
        }
    }
    ,
    Comment{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.current();
            switch (c) {
                case '-': {
                    t.advanceTransition(CommentEndDash);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    r.advance();
                    t.commentPending.append('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.commentPending.append(r.consumeToAny('-', '\u0000'));
                }
            }
        }
    }
    ,
    CommentEndDash{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-': {
                    t.transition(CommentEnd);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.commentPending.append('-').append('\ufffd');
                    t.transition(Comment);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.commentPending.append('-').append(c);
                    t.transition(Comment);
                }
            }
        }
    }
    ,
    CommentEnd{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '>': {
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.commentPending.append("--").append('\ufffd');
                    t.transition(Comment);
                    break;
                }
                case '!': {
                    t.error(this);
                    t.transition(CommentEndBang);
                    break;
                }
                case '-': {
                    t.error(this);
                    t.commentPending.append('-');
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.commentPending.append("--").append(c);
                    t.transition(Comment);
                }
            }
        }
    }
    ,
    CommentEndBang{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-': {
                    t.commentPending.append("--!");
                    t.transition(CommentEndDash);
                    break;
                }
                case '>': {
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.commentPending.append("--!").append('\ufffd');
                    t.transition(Comment);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.commentPending.append("--!").append(c);
                    t.transition(Comment);
                }
            }
        }
    }
    ,
    Doctype{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(BeforeDoctypeName);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                }
                case '>': {
                    t.error(this);
                    t.createDoctypePending();
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.transition(BeforeDoctypeName);
                }
            }
        }
    }
    ,
    BeforeDoctypeName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createDoctypePending();
                t.transition(DoctypeName);
                return;
            }
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.createDoctypePending();
                    t.doctypePending.name.append('\ufffd');
                    t.transition(DoctypeName);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.createDoctypePending();
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.createDoctypePending();
                    t.doctypePending.name.append(c);
                    t.transition(DoctypeName);
                }
            }
        }
    }
    ,
    DoctypeName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.doctypePending.name.append(name);
                return;
            }
            char c = r.consume();
            switch (c) {
                case '>': {
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(AfterDoctypeName);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.doctypePending.name.append('\ufffd');
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.doctypePending.name.append(c);
                }
            }
        }
    }
    ,
    AfterDoctypeName{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                return;
            }
            if (r.matchesAny('\t', '\n', '\r', '\f', ' ')) {
                r.advance();
            } else if (r.matches('>')) {
                t.emitDoctypePending();
                t.advanceTransition(Data);
            } else if (r.matchConsumeIgnoreCase("PUBLIC")) {
                t.doctypePending.pubSysKey = "PUBLIC";
                t.transition(AfterDoctypePublicKeyword);
            } else if (r.matchConsumeIgnoreCase("SYSTEM")) {
                t.doctypePending.pubSysKey = "SYSTEM";
                t.transition(AfterDoctypeSystemKeyword);
            } else {
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.advanceTransition(BogusDoctype);
            }
        }
    }
    ,
    AfterDoctypePublicKeyword{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(BeforeDoctypePublicIdentifier);
                    break;
                }
                case '\"': {
                    t.error(this);
                    t.transition(DoctypePublicIdentifier_doubleQuoted);
                    break;
                }
                case '\'': {
                    t.error(this);
                    t.transition(DoctypePublicIdentifier_singleQuoted);
                    break;
                }
                case '>': {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                }
            }
        }
    }
    ,
    BeforeDoctypePublicIdentifier{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    break;
                }
                case '\"': {
                    t.transition(DoctypePublicIdentifier_doubleQuoted);
                    break;
                }
                case '\'': {
                    t.transition(DoctypePublicIdentifier_singleQuoted);
                    break;
                }
                case '>': {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                }
            }
        }
    }
    ,
    DoctypePublicIdentifier_doubleQuoted{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\"': {
                    t.transition(AfterDoctypePublicIdentifier);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.doctypePending.publicIdentifier.append('\ufffd');
                    break;
                }
                case '>': {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.doctypePending.publicIdentifier.append(c);
                }
            }
        }
    }
    ,
    DoctypePublicIdentifier_singleQuoted{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\'': {
                    t.transition(AfterDoctypePublicIdentifier);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.doctypePending.publicIdentifier.append('\ufffd');
                    break;
                }
                case '>': {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.doctypePending.publicIdentifier.append(c);
                }
            }
        }
    }
    ,
    AfterDoctypePublicIdentifier{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(BetweenDoctypePublicAndSystemIdentifiers);
                    break;
                }
                case '>': {
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\"': {
                    t.error(this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    break;
                }
                case '\'': {
                    t.error(this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                }
            }
        }
    }
    ,
    BetweenDoctypePublicAndSystemIdentifiers{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    break;
                }
                case '>': {
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\"': {
                    t.error(this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    break;
                }
                case '\'': {
                    t.error(this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                }
            }
        }
    }
    ,
    AfterDoctypeSystemKeyword{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(BeforeDoctypeSystemIdentifier);
                    break;
                }
                case '>': {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\"': {
                    t.error(this);
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    break;
                }
                case '\'': {
                    t.error(this);
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                }
            }
        }
    }
    ,
    BeforeDoctypeSystemIdentifier{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    break;
                }
                case '\"': {
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    break;
                }
                case '\'': {
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    break;
                }
                case '>': {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
                }
            }
        }
    }
    ,
    DoctypeSystemIdentifier_doubleQuoted{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\"': {
                    t.transition(AfterDoctypeSystemIdentifier);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.doctypePending.systemIdentifier.append('\ufffd');
                    break;
                }
                case '>': {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.doctypePending.systemIdentifier.append(c);
                }
            }
        }
    }
    ,
    DoctypeSystemIdentifier_singleQuoted{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\'': {
                    t.transition(AfterDoctypeSystemIdentifier);
                    break;
                }
                case '\u0000': {
                    t.error(this);
                    t.doctypePending.systemIdentifier.append('\ufffd');
                    break;
                }
                case '>': {
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.doctypePending.systemIdentifier.append(c);
                }
            }
        }
    }
    ,
    AfterDoctypeSystemIdentifier{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    break;
                }
                case '>': {
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.error(this);
                    t.transition(BogusDoctype);
                }
            }
        }
    }
    ,
    BogusDoctype{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '>': {
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
                case '\uffff': {
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                }
            }
        }
    }
    ,
    CdataSection{

        @Override
        void read(Tokeniser t, CharacterReader r) {
            String data = r.consumeTo("]]>");
            t.dataBuffer.append(data);
            if (r.matchConsume("]]>") || r.isEmpty()) {
                t.emit(new Token.CData(t.dataBuffer.toString()));
                t.transition(Data);
            }
        }
    };

    static final char nullChar = '\u0000';
    static final char[] attributeSingleValueCharsSorted;
    static final char[] attributeDoubleValueCharsSorted;
    static final char[] attributeNameCharsSorted;
    static final char[] attributeValueUnquoted;
    private static final char replacementChar = '\ufffd';
    private static final String replacementStr;
    private static final char eof = '\uffff';

    abstract void read(Tokeniser var1, CharacterReader var2);

    private static void handleDataEndTag(Tokeniser t, CharacterReader r, TokeniserState elseTransition) {
        if (r.matchesLetter()) {
            String name = r.consumeLetterSequence();
            t.tagPending.appendTagName(name);
            t.dataBuffer.append(name);
            return;
        }
        boolean needsExitTransition = false;
        if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
            char c = r.consume();
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    t.transition(BeforeAttributeName);
                    break;
                }
                case '/': {
                    t.transition(SelfClosingStartTag);
                    break;
                }
                case '>': {
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                }
                default: {
                    t.dataBuffer.append(c);
                    needsExitTransition = true;
                    break;
                }
            }
        } else {
            needsExitTransition = true;
        }
        if (needsExitTransition) {
            t.emit("</" + t.dataBuffer.toString());
            t.transition(elseTransition);
        }
    }

    private static void readRawData(Tokeniser t, CharacterReader r, TokeniserState current, TokeniserState advance) {
        switch (r.current()) {
            case '<': {
                t.advanceTransition(advance);
                break;
            }
            case '\u0000': {
                t.error(current);
                r.advance();
                t.emit('\ufffd');
                break;
            }
            case '\uffff': {
                t.emit(new Token.EOF());
                break;
            }
            default: {
                String data = r.consumeRawData();
                t.emit(data);
            }
        }
    }

    private static void readCharRef(Tokeniser t, TokeniserState advance) {
        int[] c = t.consumeCharacterReference(null, false);
        if (c == null) {
            t.emit('&');
        } else {
            t.emit(c);
        }
        t.transition(advance);
    }

    private static void readEndTag(Tokeniser t, CharacterReader r, TokeniserState a, TokeniserState b) {
        if (r.matchesLetter()) {
            t.createTagPending(false);
            t.transition(a);
        } else {
            t.emit("</");
            t.transition(b);
        }
    }

    private static void handleDataDoubleEscapeTag(Tokeniser t, CharacterReader r, TokeniserState primary, TokeniserState fallback) {
        if (r.matchesLetter()) {
            String name = r.consumeLetterSequence();
            t.dataBuffer.append(name);
            t.emit(name);
            return;
        }
        char c = r.consume();
        switch (c) {
            case '\t': 
            case '\n': 
            case '\f': 
            case '\r': 
            case ' ': 
            case '/': 
            case '>': {
                if (t.dataBuffer.toString().equals("script")) {
                    t.transition(primary);
                } else {
                    t.transition(fallback);
                }
                t.emit(c);
                break;
            }
            default: {
                r.unconsume();
                t.transition(fallback);
            }
        }
    }

    static {
        attributeSingleValueCharsSorted = new char[]{'\u0000', '&', '\''};
        attributeDoubleValueCharsSorted = new char[]{'\u0000', '\"', '&'};
        attributeNameCharsSorted = new char[]{'\u0000', '\t', '\n', '\f', '\r', ' ', '\"', '\'', '/', '<', '=', '>'};
        attributeValueUnquoted = new char[]{'\u0000', '\t', '\n', '\f', '\r', ' ', '\"', '&', '\'', '<', '=', '>', '`'};
        replacementStr = String.valueOf('\ufffd');
    }
}

