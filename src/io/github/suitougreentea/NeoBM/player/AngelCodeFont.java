package io.github.suitougreentea.NeoBM.player;

import io.github.suitougreentea.util.Image;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AngelCodeFont {
    private GameRenderer renderer;

    private int lineHeight;
    private int base;
    private int scaleW;
    private int scaleH;
    private Image[] pages;

    private Map<Integer, Glyph> chars = new HashMap<Integer, Glyph>();

    public AngelCodeFont(GameRenderer r, String fntPath) throws IOException{
        this.renderer = r;
        try {
            parseFnt(fntPath);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void parseFnt(String path) throws IOException, SAXException, ParserConfigurationException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder document = factory.newDocumentBuilder();
        Node root = document.parse(path);

        Node font = root.getFirstChild();
        NodeList fontchild = font.getChildNodes();
        for(int i=0;i<fontchild.getLength();i++){
            Node node = fontchild.item(i);
            if(node.getNodeName() == "info"){

            }else if(node.getNodeName() == "common"){
                NamedNodeMap attr = node.getAttributes();
                Node lineHeightNode = attr.getNamedItem("lineHeight");
                if(lineHeightNode != null) lineHeight = Integer.parseInt(lineHeightNode.getNodeValue());
                Node baseNode = attr.getNamedItem("base");
                if(baseNode != null) base = Integer.parseInt(baseNode.getNodeValue());
                Node scaleWNode = attr.getNamedItem("scaleW");
                if(scaleWNode != null) scaleW = Integer.parseInt(scaleWNode.getNodeValue());
                Node scaleHNode = attr.getNamedItem("scaleH");
                if(scaleHNode != null) scaleH = Integer.parseInt(scaleHNode.getNodeValue());
                Node pagesNode = attr.getNamedItem("pages");
                if(pagesNode != null) pages = new Image[Integer.parseInt(pagesNode.getNodeValue())];
            }else if(node.getNodeName() == "pages"){
                NodeList pagesNodeList = node.getChildNodes();
                for(int j=0;j<pagesNodeList.getLength();j++){
                    Node pageNode = pagesNodeList.item(j);
                    if(pageNode.getNodeName() == "page"){
                        NamedNodeMap pageAttr = pageNode.getAttributes();
                        Node idNode = pageAttr.getNamedItem("id");
                        Node fileNode = pageAttr.getNamedItem("file");
                        if(idNode != null && fileNode != null){
                            File file = new File(path).getAbsoluteFile();
                            String parentPath = file.getParent();
                            pages[Integer.parseInt(idNode.getNodeValue())] = new Image(parentPath + "/" + fileNode.getNodeValue());
                        }
                    }
                }
            }else if(node.getNodeName() == "chars"){
                NodeList charsNodeList = node.getChildNodes();
                for(int j=0;j<charsNodeList.getLength();j++){
                    Node charNode = charsNodeList.item(j);
                    if(charNode.getNodeName() == "char"){
                        NamedNodeMap charAttr = charNode.getAttributes();
                        Node idNode = charAttr.getNamedItem("id");
                        Node xNode = charAttr.getNamedItem("x");
                        Node yNode = charAttr.getNamedItem("y");
                        Node widthNode = charAttr.getNamedItem("width");
                        Node heightNode = charAttr.getNamedItem("height");
                        Node xOffsetNode = charAttr.getNamedItem("xoffset");
                        Node yOffsetNode = charAttr.getNamedItem("yoffset");
                        Node xAdvanceNode = charAttr.getNamedItem("xadvance");
                        Node pageNode = charAttr.getNamedItem("page");
                        if(
                                idNode != null
                                && xNode != null
                                && yNode != null
                                && widthNode != null
                                && heightNode != null
                                && xOffsetNode != null
                                && yOffsetNode != null
                                && xAdvanceNode != null
                                && pageNode != null
                                ){
                            chars.put(Integer.parseInt(idNode.getNodeValue()), 
                                    new Glyph(Integer.parseInt(xNode.getNodeValue()),
                                            Integer.parseInt(yNode.getNodeValue()),
                                            Integer.parseInt(widthNode.getNodeValue()),
                                            Integer.parseInt(heightNode.getNodeValue()),
                                            Integer.parseInt(xOffsetNode.getNodeValue()),
                                            Integer.parseInt(yOffsetNode.getNodeValue()),
                                            Integer.parseInt(xAdvanceNode.getNodeValue()),
                                            pages[Integer.parseInt(pageNode.getNodeValue())])
                                    );
                        }
                    }
                }
            }else if(node.getNodeName() == "kernings"){

            }
        }
    }

    public void drawString(String str, int x, int y){
        int cx = 0;
        for(int i=0;i<str.length();i++){
            int c = str.charAt(i);
            Glyph glyph = chars.get(c);
            if(glyph != null){
                renderer.drawImage(
                        glyph.getImage(),
                        x + cx + glyph.getXoffset(),
                        y + glyph.getYoffset() + lineHeight - base,
                        glyph.getWidth(),
                        glyph.getHeight(),
                        glyph.getX(),
                        glyph.getY()
                        );
                cx += glyph.getXadvance() + 1;
            }
        }
    }
}

class Glyph {
    private Image image;
    private int x;
    private int y;
    private int width;
    private int height;
    private int xoffset;
    private int yoffset;
    private int xadvance;

    public Glyph(int x, int y, int width, int height, int xoffset, int yoffset, int xadvance, Image image) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
        this.xadvance = xadvance;
        this.image = image;
    }

    private Map<Integer, Integer> kerning;

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXoffset() {
        return xoffset;
    }

    public int getYoffset() {
        return yoffset;
    }

    public int getXadvance() {
        return xadvance;
    }
}
