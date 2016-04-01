package com.erlema.util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Bxtyfufff on 2016/2/26 0026.
 */
public class Utils {
    /**
     * 方法名称:transStringToMap
     * 传入参数:mapString 形如 username'chenziwen^password'1234
     * 返回值:Map
     */
    public static Map transStringToMap(String mapString){
        Map map = new HashMap();
        java.util.StringTokenizer items;
        for(StringTokenizer entrys = new StringTokenizer(mapString, ",");entrys.hasMoreTokens();
            map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), ":");
        return map;
    }
}
