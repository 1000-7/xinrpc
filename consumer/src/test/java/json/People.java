package json;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class People{
	Long id;
	String name;
	String address;
	List<Phone> phoneList;
	Map<String, JSONArray> jsonArrayMap;
}
