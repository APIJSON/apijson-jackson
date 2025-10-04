/*Copyright ©2025 APIJSON(https://github.com/APIJSON)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package apijson.jackson;

import apijson.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


/**启动入口 Application
 * 调用 APIJSONApplication.init
 * @author Lemon
 */
public class APIJSONApplication extends apijson.framework.APIJSONApplication {

    public static ObjectMapper MAPPER;
    public static TypeReference<LinkedHashMap<String, Object>> JSON_OBJECT_REFERENCE;
    public static Class<?> JSON_OBJECT_CLASS;
    public static TypeReference<ArrayList<Object>> JSON_ARRAY_REFERENCE;
    public static Class<?> JSON_ARRAY_CLASS;
    static {
        //APIJSONParser.IS_RETURN_STACK_TRACE = false;

        MAPPER = new ObjectMapper();

        JSON_OBJECT_REFERENCE = new TypeReference<LinkedHashMap<String, Object>>(){};
        JSON_OBJECT_CLASS = JSON_OBJECT_REFERENCE.getType().getClass();

        JSON_ARRAY_REFERENCE = new TypeReference<ArrayList<Object>>(){};
        JSON_ARRAY_CLASS = JSON_ARRAY_REFERENCE.getType().getClass();

        // apijson.JSON.DEFAULT_JSON_PARSER = JSON.DEFAULT_JSON_PARSER; // 解决 DEFAULT_JSON_PARSER 初始化前就自测导致抛异常

        DEFAULT_APIJSON_CREATOR = new APIJSONCreator<>();

        JSON.DEFAULT_JSON_PARSER = new JSONParser() {
            @Override
            public Map<String, Object> createJSONObject() {
                return JSONParser.super.createJSONObject();
            }

            @Override
            public List<Object> createJSONArray() {
                return JSONParser.super.createJSONArray();
            }

            @Override
            public String toJSONString(Object obj, boolean format) {
                if (obj instanceof String) {
                    if (! format) {
                        return (String) obj;
                    }

                    obj = parse(obj);
                }

                try {
                    if (format) { // TODO 格式化
                        return MAPPER.writeValueAsString(obj);
                    }
                    return MAPPER.writeValueAsString(obj);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Object parse(Object json) {
                try {
                    return MAPPER.readValue(toJSONString(json), Object.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Map<String, Object> parseObject(Object json) {
                try {
                    return MAPPER.readValue(toJSONString(json), JSON_OBJECT_REFERENCE);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public <T> T parseObject(Object json, Class<T> clazz) {
                try {
                    return MAPPER.readValue(toJSONString(json), clazz);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public List<Object> parseArray(Object json) {
                try {
                    return MAPPER.readValue(toJSONString(json), JSON_ARRAY_REFERENCE);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public <T> List<T> parseArray(Object json, Class<T> clazz) {
                List<Object> list = parseArray(json);
                if (list == null) {
                    return null;
                }

                List<T> list2 = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    Object obj = list.get(i);
                    if (obj != null && clazz != null && ! clazz.isAssignableFrom(obj.getClass())) {
                        String str = toJSONString(obj);
                        try {
                            obj = clazz.isAssignableFrom(String.class) ? str : MAPPER.readValue(str, clazz);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    list2.add((T) obj);
                }

                return list2;
            }
        };
    }


    //public static <T> APIJSONParser<T> createParser() {
    //    return (APIJSONParser<T>) DEFAULT_APIJSON_CREATOR.createParser();
    //}

    /**初始化，加载所有配置并校验
     * @return
     * @throws Exception
     */
    public static void init() throws Exception {
        init(true, DEFAULT_APIJSON_CREATOR);
    }
    /**初始化，加载所有配置并校验
     * @param shutdownWhenServerError
     * @return
     * @throws Exception
     */
    public static void init(boolean shutdownWhenServerError) throws Exception {
        init(shutdownWhenServerError, DEFAULT_APIJSON_CREATOR);
    }
    /**初始化，加载所有配置并校验
     * @param creator
     * @return
     * @throws Exception
     */
    public static <T> void init(@NotNull APIJSONCreator<T> creator) throws Exception {
        init(true, creator);
    }
    /**初始化，加载所有配置并校验
     * @param shutdownWhenServerError
     * @param creator
     * @return
     * @throws Exception
     */
    public static <T> void init(boolean shutdownWhenServerError, @NotNull APIJSONCreator<T> creator) throws Exception {
        apijson.framework.APIJSONApplication.init(shutdownWhenServerError, creator);
    }

}