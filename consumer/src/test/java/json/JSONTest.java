package json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONTest {

	public static void main(String[] args) {
		Phone phone1 = new Phone(1L, "aa");
		Phone phone2 = new Phone(2L, "bb");
		List<Phone> phoneList = new ArrayList<>();
		phoneList.add(phone1);
		phoneList.add(phone2);

		People people = new People();
		people.setId(1L);
		people.setAddress("address");
		people.setName("aaaa");
		people.setPhoneList(phoneList);
		JSONArray jsonArray1 = new JSONArray();
		jsonArray1.add(1);
		jsonArray1.add(2);
		if (people.getJsonArrayMap() == null) {
			people.setJsonArrayMap(new HashMap<>());
		}
		people.getJsonArrayMap().put("12313", jsonArray1);
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(people);
		String jsonString = jsonArray.toJSONString();
		System.out.println(jsonString);
		List<People> people1 = JSON.parseArray(jsonString, People.class);
		System.out.println(people1);
	}

	@Test
	public void method(){
		String s = "{\"address\":\"address\",\"id\":1,\"jsonArrayMap\":{\"12313\":[1,2]},\"name\":\"aaaa\",\"phoneList\":[{\"des\":\"aa\",\"id\":1},{\"des\":\"bb\",\"id\":2}]}";
		JSONObject jsonObject = JSON.parseObject(s);

	}




}


