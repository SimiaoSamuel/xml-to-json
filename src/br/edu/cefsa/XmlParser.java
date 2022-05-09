package br.edu.cefsa;

import java.util.*;

import static java.util.UUID.randomUUID;

public class XmlParser {

    Map<String, Object> map = new HashMap<>();

    public String toJson(String xml) {
        var root =  new ArrayList<String>();
        var childrenRoot = new ArrayList<String>();

        var tag = xml.split(">");
        Arrays.stream(tag).forEach(token -> {
            token = token.strip();
            if (token.startsWith("<") && !token.startsWith("</") && map.isEmpty()){
                String replace = token.replace("<", "");
                map.put(replace, new HashMap<String, Object>());
                root.add(replace);
            } else if (token.startsWith("<") && !token.startsWith("</") && !map.isEmpty()){
                var childrenList = (HashMap<String, Object>) map.get(root.get(root.size() - 1));
                String replace = token.replace("<", "");
                if (childrenRoot.size() >= 1 && childrenList.containsKey(childrenRoot.get(childrenRoot.size() - 1))){
                    var dale = (HashMap<String, Object>) childrenList.get(childrenRoot.get(childrenRoot.size() - 1));
                    if (dale != null && !childrenList.containsKey(replace)) {
                        dale.put(replace, new HashMap<String, Object>());
                    } else if (childrenList.containsKey(replace)){
                        String tokenReplaced = replace + "|" + randomUUID();
                        childrenList.put(tokenReplaced, new HashMap<String, Object>());
                        childrenRoot.add(tokenReplaced);
                    }
                } else {
                    if (childrenList.containsKey(replace)){
                        childrenList.put(replace + "|" + randomUUID(), new HashMap<String, Object>());
                    } else {
                        childrenList.put(replace, new HashMap<String, Object>());
                    }
                    childrenRoot.add(replace);
                }
            } else if (!token.startsWith("<")){
                String[] split = token.split("/");
                String tokenConversion = split[0].replace("<", "");
                Object targetTag = map.get(split[1]);
                if (targetTag != null){
                    ((HashMap<String, Object>) targetTag).put(tokenConversion, tokenConversion);
                } else {
                    extracted(map, tokenConversion, split[1], childrenRoot.get(childrenRoot.size() - 1));
                }
            }
        });

        return xmlToJson();
    }

    private String xmlToJson(){
        StringBuilder json = new StringBuilder();
        json.append("{");

        extracted(json, map);
        json.append("}");
        return json.toString().replace(",}","}").replace(",{}","");
    }

    private Integer sizeOfFutureMap(HashMap<String, Object> jsonMap){
        Object o = jsonMap.values().stream().findFirst().orElseThrow();
        if (o instanceof HashMap){
            return ((HashMap<?, ?>) o).size();
        }
        return 0;
    }

    private void extracted(StringBuilder json, Map<String, Object> mapper) {
        mapper.forEach((k, v) -> {
            if (v instanceof HashMap){
                HashMap<String, Object> jsonMap = (HashMap<String, Object>) v;
                if (jsonMap.size() > 1 && sizeOfFutureMap(jsonMap) > 1){
                    var o = jsonMap.keySet().stream().findFirst().orElseThrow();

                    json.append("\"" + k + "\":{\"" + o.split("\\|")[0] + "\":[{");
                    extracted(json, jsonMap);
                    json.append("}]},");
                } else if (jsonMap.size() > 1 && sizeOfFutureMap(jsonMap) <= 1) {
                    extracted(json, jsonMap);
                    json.append("},{");
                } else {
                    json.append("\"" + k + "\":\""+((HashMap<?, ?>) v).values().stream().findFirst().orElseThrow()+"\",");
                }
            } else {
                json.append("\"" + k + "\",");
            }
        });
    }

    private void extracted(Map<String, Object> mapValue, String tokenConversion, String tokenMap, String rootTag) {
        mapValue.forEach((k, v) -> {
            if (v instanceof HashMap){
                var targetTag = ((HashMap<String, Object>) v).get(tokenMap);
                if (targetTag != null && k.equalsIgnoreCase(rootTag)){
                    ((HashMap<String, Object>) targetTag).put(tokenConversion, tokenConversion);
                } else if (!mapValue.isEmpty()){
                    extracted((HashMap<String,Object>)v, tokenConversion, tokenMap, rootTag);
                }
            }
        });
    }


}
