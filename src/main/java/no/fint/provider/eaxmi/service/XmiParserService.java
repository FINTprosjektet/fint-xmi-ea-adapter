package no.fint.provider.eaxmi.service;

import lombok.Data;
import net.sf.saxon.tree.tiny.TinyElementImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.List;

@Service
@Data
public class XmiParserService {

    @Autowired
    XPathService xpath;

    private List packages;
    private List classes;
    private List associations;
    private List generalizations;


    public void getXmiDocument() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("FINT-informasjonsmodell.xml").getFile());
        xpath.initializeSAXParser(file);

        packages = xpath.getNodeList("//elements/element[@xmi:type=\"uml:Package\"][@name!=\"Model\"]");
        classes = xpath.getNodeList("//elements/element[@xmi:type=\"uml:Class\"]");
        associations = xpath.getNodeList("//connectors/connector/properties[@ea_type='Association']/..");
        generalizations = xpath.getNodeList("//connectors/connector/properties[@ea_type='Generalization']/..");


    }

    public String getInheritFromId(String idref) throws XPathExpressionException {

        return xpath.getStringValue(
                String.format("//connectors/connector/properties[@ea_type='Generalization']/../source[@xmi:idref='%s']/../target/@xmi:idref", idref)
        );
    }


    public String getIdRefFromNode(TinyElementImpl node) {
        return xpath.getStringValue(node, "@xmi:idref");
    }

    public List getClassesInPackage(String idref) throws XPathExpressionException {
        return xpath.getNodeList(String.format("//element[@xmi:type=\"uml:Class\"]/model[@package=\"%s\"]/..", idref));
    }

    public String getParentPackageFromNode(TinyElementImpl node) {
        return xpath.getStringValue(node, "model/@package");
    }

    public String getParentPackageByIdRef(String idref) {
        return xpath.getStringValue(String.format("//element[@xmi:idref=\"%s\"]/model/@package", idref));
    }

    public String getName(String idref) {
        return xpath.getStringValue(String.format("//element[@xmi:idref=\"%s\"]/@name", idref));
    }

    public List getClassRelations(String idref) {
        return xpath.getNodeList(String.format("//connector/properties[@ea_type='Association']/../source[@xmi:idref='%s']/..", idref));
    }



    public TinyElementImpl getRelationSource(String idref) {
        return xpath.getNode(String.format("//connector/properties[@ea_type='Association']/..[@xmi:idref='%s']/source", idref));
    }


    public TinyElementImpl getRelationTarget(String idref) {
        return xpath.getNode(String.format("//connector/properties[@ea_type='Association']/..[@xmi:idref='%s']/target", idref));
    }

}
