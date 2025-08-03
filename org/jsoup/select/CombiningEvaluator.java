/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;

abstract class CombiningEvaluator
extends Evaluator {
    final ArrayList<Evaluator> evaluators = new ArrayList();
    int num = 0;

    CombiningEvaluator() {
    }

    CombiningEvaluator(Collection<Evaluator> evaluators) {
        this();
        this.evaluators.addAll(evaluators);
        this.updateNumEvaluators();
    }

    Evaluator rightMostEvaluator() {
        return this.num > 0 ? this.evaluators.get(this.num - 1) : null;
    }

    void replaceRightMostEvaluator(Evaluator replacement) {
        this.evaluators.set(this.num - 1, replacement);
    }

    void updateNumEvaluators() {
        this.num = this.evaluators.size();
    }

    static final class Or
    extends CombiningEvaluator {
        Or(Collection<Evaluator> evaluators) {
            if (this.num > 1) {
                this.evaluators.add(new And(evaluators));
            } else {
                this.evaluators.addAll(evaluators);
            }
            this.updateNumEvaluators();
        }

        Or(Evaluator ... evaluators) {
            this(Arrays.asList(evaluators));
        }

        Or() {
        }

        public void add(Evaluator e) {
            this.evaluators.add(e);
            this.updateNumEvaluators();
        }

        @Override
        public boolean matches(Element root, Element node) {
            for (int i = 0; i < this.num; ++i) {
                Evaluator s = (Evaluator)this.evaluators.get(i);
                if (!s.matches(root, node)) continue;
                return true;
            }
            return false;
        }

        public String toString() {
            return StringUtil.join(this.evaluators, ", ");
        }
    }

    static final class And
    extends CombiningEvaluator {
        And(Collection<Evaluator> evaluators) {
            super(evaluators);
        }

        And(Evaluator ... evaluators) {
            this(Arrays.asList(evaluators));
        }

        @Override
        public boolean matches(Element root, Element node) {
            for (int i = 0; i < this.num; ++i) {
                Evaluator s = (Evaluator)this.evaluators.get(i);
                if (s.matches(root, node)) continue;
                return false;
            }
            return true;
        }

        public String toString() {
            return StringUtil.join(this.evaluators, " ");
        }
    }
}

