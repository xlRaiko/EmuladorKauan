/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users.clothingvalidation;

import com.eu.habbo.habbohotel.users.clothingvalidation.FiguredataPalette;
import com.eu.habbo.habbohotel.users.clothingvalidation.FiguredataPaletteColor;
import com.eu.habbo.habbohotel.users.clothingvalidation.FiguredataSettype;
import com.eu.habbo.habbohotel.users.clothingvalidation.FiguredataSettypeSet;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Figuredata {
    public Map<Integer, FiguredataPalette> palettes = new TreeMap<Integer, FiguredataPalette>();
    public Map<String, FiguredataSettype> settypes = new TreeMap<String, FiguredataSettype>();

    public void parseXML(String uri) throws Exception, ParserConfigurationException, IOException, SAXException {
        Element element;
        Node nNode;
        int i;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(uri);
        Element rootElement = document.getDocumentElement();
        if (!rootElement.getTagName().equalsIgnoreCase("figuredata") || document.getElementsByTagName("colors") == null || document.getElementsByTagName("sets") == null) {
            StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(writer));
            String documentString = writer.getBuffer().toString();
            throw new Exception("The passed file is not in figuredata format. Received " + documentString.substring(0, Math.min(documentString.length(), 200)));
        }
        NodeList palettesList = document.getElementsByTagName("colors").item(0).getChildNodes();
        NodeList settypesList = document.getElementsByTagName("sets").item(0).getChildNodes();
        this.palettes.clear();
        this.settypes.clear();
        for (i = 0; i < palettesList.getLength(); ++i) {
            nNode = palettesList.item(i);
            if (nNode.getNodeType() != 1) continue;
            element = (Element)nNode;
            int paletteId = Integer.parseInt(element.getAttribute("id"));
            FiguredataPalette palette = new FiguredataPalette(paletteId);
            NodeList colorsList = nNode.getChildNodes();
            for (int ii = 0; ii < colorsList.getLength(); ++ii) {
                if (colorsList.item(ii).getNodeType() != 1) continue;
                Element colorElement = (Element)colorsList.item(ii);
                FiguredataPaletteColor color = new FiguredataPaletteColor(Integer.parseInt(colorElement.getAttribute("id")), Integer.parseInt(colorElement.getAttribute("index")), !colorElement.getAttribute("club").equals("0"), colorElement.getAttribute("selectable").equals("1"), colorElement.getTextContent());
                palette.addColor(color);
            }
            this.palettes.put(palette.id, palette);
        }
        for (i = 0; i < settypesList.getLength(); ++i) {
            nNode = settypesList.item(i);
            if (nNode.getNodeType() != 1) continue;
            element = (Element)nNode;
            String type = element.getAttribute("type");
            int paletteId = Integer.parseInt(element.getAttribute("paletteid"));
            boolean mandM0 = element.getAttribute("mand_m_0").equals("1");
            boolean mandF0 = element.getAttribute("mand_f_0").equals("1");
            boolean mandM1 = element.getAttribute("mand_m_1").equals("1");
            boolean mandF1 = element.getAttribute("mand_f_1").equals("1");
            FiguredataSettype settype = new FiguredataSettype(type, paletteId, mandM0, mandF0, mandM1, mandF1);
            NodeList setsList = nNode.getChildNodes();
            for (int ii = 0; ii < setsList.getLength(); ++ii) {
                if (setsList.item(ii).getNodeType() != 1) continue;
                Element setElement = (Element)setsList.item(ii);
                FiguredataSettypeSet set = new FiguredataSettypeSet(Integer.parseInt(setElement.getAttribute("id")), setElement.getAttribute("gender"), !setElement.getAttribute("club").equals("0"), setElement.getAttribute("colorable").equals("1"), setElement.getAttribute("selectable").equals("1"), setElement.getAttribute("preselectable").equals("1"), setElement.getAttribute("sellable").equals("1"));
                settype.addSet(set);
            }
            this.settypes.put(settype.type, settype);
        }
    }
}

