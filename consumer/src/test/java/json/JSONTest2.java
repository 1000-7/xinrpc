package json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;

public class JSONTest2 {
	private static final String BLOCK_NAME_KEY = "blockId";
	private static final String BUCKET_ID_KEY = "bucketId";
	private static final String ONE_BUCKET_DATA_KEY = "bucketData";
	private static final String ALL_DATA_KEY = "blockData";
	private static final String ONT_BLOCK_DATA_KEY = "blockTypeData";
	private static final String PV = "pv";
	private static final String UV = "uv";

	@Test
	public void method() {
		Table<Long, Long, JSONObject> realTimeData = HashBasedTable.create();
		realTimeData.put(1L, 1L, mock(100));
		realTimeData.put(1L, 2L, mock(110));
		realTimeData.put(2L, 1L, mock(120));
		realTimeData.put(2L, 2L, mock(130));

		JSONArray jsonArray = transferRealTimeData(realTimeData);
		System.out.println(jsonArray);

	}

	private JSONObject mock(int n) {
		JSONObject js = new JSONObject();
		JSONArray ja1 = new JSONArray();
		ja1.add(new JSONObject().fluentPut("1566981310000", new Random().nextInt(n * 10)));
		ja1.add(new JSONObject().fluentPut("1566981320000", new Random().nextInt(n * 10)));
		ja1.add(new JSONObject().fluentPut("1566981330000", new Random().nextInt(n * 10)));
		ja1.add(new JSONObject().fluentPut("1566981340000", new Random().nextInt(n * 10)));
		ja1.add(new JSONObject().fluentPut("1566981350000", new Random().nextInt(n * 10)));
		ja1.add(new JSONObject().fluentPut("1566981360000", new Random().nextInt(n * 10)));
		js.put(PV, ja1);
		JSONArray ja2 = new JSONArray();
		ja2.add(new JSONObject().fluentPut("1566981310000", new Random().nextInt(n)));
		ja2.add(new JSONObject().fluentPut("1566981320000", new Random().nextInt(n)));
		ja2.add(new JSONObject().fluentPut("1566981330000", new Random().nextInt(n)));
		ja2.add(new JSONObject().fluentPut("1566981340000", new Random().nextInt(n)));
		ja2.add(new JSONObject().fluentPut("1566981350000", new Random().nextInt(n)));
		ja2.add(new JSONObject().fluentPut("156698130000", new Random().nextInt(n)));
		js.put(UV, ja2);
		return js;
	}

	private JSONArray transferRealTimeData(Table<Long, Long, JSONObject> realTimeData) {
		JSONArray res = new JSONArray();
		Map<Long, Map<Long, JSONObject>> realTimeDataMap = realTimeData.rowMap();
		realTimeDataMap.forEach((s, map) -> {
			JSONObject oneBlockData = new JSONObject();
			oneBlockData.put(BLOCK_NAME_KEY, s);
			Table<String, Long, Object> bucketData = HashBasedTable.create();

			map.forEach((bucketId, data) -> {
				Map<String, Object> innerMap = data.getInnerMap();
				innerMap.forEach((dataName, oneDataArray) -> {
					bucketData.put(dataName, bucketId, oneDataArray);
				});
			});
			JSONArray ja = new JSONArray();

			bucketData.rowMap().forEach((dataName, bucketIdMap) -> {
				JSONArray jsonArray = new JSONArray();
				bucketIdMap.forEach((aLong, o) -> {
					JSONObject bucketIdData = new JSONObject();
					bucketIdData.put(ONE_BUCKET_DATA_KEY, o);
					bucketIdData.put(BUCKET_ID_KEY, aLong);
					jsonArray.add(bucketIdData);
				});
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", dataName);
				jsonObject.put(ONT_BLOCK_DATA_KEY, jsonArray);
				ja.add(jsonObject);
			});
			oneBlockData.put(ALL_DATA_KEY, ja);

			res.add(oneBlockData);
		});

		return res;
	}

}
