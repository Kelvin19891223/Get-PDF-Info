/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author Kelvi
 */
public class Test {
    public static void listFields(PDDocument doc) throws Exception {
        PDDocumentCatalog catalog = doc.getDocumentCatalog();
        PDAcroForm form = catalog.getAcroForm();
        List<PDField> fields = form.getFields();

        JSONArray items = new JSONArray();
        String previous_name = "";
        
        for(int i=0; i<fields.size(); i++) {
            PDField field = fields.get(i);
            
        //for(PDField field: fields) {
            //HashMap<String, Object> item = new HashMap<String,Object>();
                        
//            Object value = field.getValueAsString();            
//            String name = field.getFullyQualifiedName();
            
//            System.out.print(type);            
//            System.out.print(name);
//            System.out.print(" = ");
//            System.out.print(value);
//            System.out.println();
//            
            Object type = field.getFieldType();                   
            
            String name = field.getFullyQualifiedName();
            
            if(name.indexOf("[") > -1)
                name = name.substring(0,name.indexOf("["));            
            
            if(!name.equals(previous_name))
            {                
                JSONObject item = new JSONObject();
                item.put("Name", name);
                previous_name = name;

                if(!type.equals("Tx"))
                {

                    try {
                        PDChoice choice = (PDChoice) field;

                        item.put("Type", "DropBox");
                        List<String> options;
                        options = choice.getOptionsDisplayValues();
                        JSONArray list = new JSONArray();
                        for(String option: options)
                        {
                            JSONObject ite = new JSONObject();
                            ite.put("title", getEscapeString(option));
                            list.add(ite);
                        }
                        item.put("List", list);
                    } catch(Exception e) {

                        try {
                            PDCheckBox chkbox = (PDCheckBox) field;
                            item.put("Type", "CheckBox");                            
                            JSONArray list = new JSONArray();
                            Set<String> rrr = chkbox.getOnValues();
                            for(String fff: rrr)
                            {
                                JSONObject ite = new JSONObject();
                                ite.put("title", getEscapeString(fff));
                                list.add(ite);                      
                            }

                            for(int k=i+1; k<fields.size(); k++) {
                                PDField field1 = fields.get(k);
                                String name1 = field1.getFullyQualifiedName();
                                if(name1.indexOf("[") > -1)                                
                                    name1 = name1.substring(0,name1.indexOf("["));
                                
                                if(name.equals(name1))
                                {                                    
                                    PDCheckBox chkbox1 = (PDCheckBox) field1;
                                    Set<String> rrr1 = chkbox1.getOnValues();
                                    for(String fff: rrr1)
                                    {
                                        JSONObject ite = new JSONObject();
                                        ite.put("title", getEscapeString(fff));
                                        list.add(ite);                            
                                    }
                                } else {
                                    break;
                                }
                            }
                            
                            item.put("List", list);
                        } catch(Exception eex)
                        {
                            try {
                                PDRadioButton radiobtn = (PDRadioButton) field;
                                item.put("Type", "RadioButton");
                                Set<String> rrr = radiobtn.getOnValues();
                                JSONArray list = new JSONArray();
                                for(String fff: rrr)
                                {
                                    JSONObject ite = new JSONObject();
                                    ite.put("title", getEscapeString(fff));
                                    list.add(ite);                                
                                }
                                item.put("List", list);
                            } catch(Exception exf) {
                                item.put("Type", "Button");
                            }
                        }
                    }
                } else {
                    item.put("Type", "Text");
                    String subName = field.getFullyQualifiedName();
            
                    if(subName.indexOf("[") > -1 && subName.indexOf("]") > -1)
                    {
                        subName = subName.substring(subName.indexOf("[")+1,subName.indexOf("]"));
                        JSONArray array = new JSONArray();
                        JSONObject sub = new JSONObject();
                        sub.put("title", subName);
                        array.add(sub);
                        JSONObject subItem = new JSONObject();
                        for(int k=i+1; k<fields.size(); k++)
                        {
                            PDField field1 = fields.get(k);
                            String name1 = field1.getFullyQualifiedName();
                            if(name1.indexOf("[") > -1 && name1.indexOf("]") > -1) {
                                
                            
                                name1 = name1.substring(0,name1.indexOf("["));
                            
                                if(name.equals(name1))
                                {
                                    subName = field1.getFullyQualifiedName();
                                    subName = subName.substring(subName.indexOf("[")+1,subName.indexOf("]"));

                                    subItem.put("title", subName);        
                                } else {
                                    break;
                                }
                                array.add(subItem);
                            } else {
                                break;
                            }                            
                        }                        
                        item.put("SubName", array);
                    }                    
                }
                items.add(item);
            }            
        }
        String result = items.toJSONString();
        System.out.println(result);
    }
    
    public static String getEscapeString(String item)
    {
        String result = "";
        result = item.replaceAll(":plus:"," ");
        result = result.replaceAll(":percentage:2C", ",");
        result = result.replaceAll(":percentage:29", ")");
        result = result.replaceAll(":percentage:28", "(");
        result = result.replaceAll(":percentage:24", "\\$");
        result = result.replaceAll(":percentage:25", "\\%");
        result = result.replaceAll(":percentage:2B", "+");
        result = result.replaceAll(":percentage:C2", "");
        result = result.replaceAll(":percentage:A0", "");
        result = result.replaceAll(":percentage:27", "'");
        
        return result;     
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        // TODO code application logic here
        File file = new File("g:\\3.pdf");
        PDDocument doc = PDDocument.load(file);
        listFields(doc);
    }
    
}
