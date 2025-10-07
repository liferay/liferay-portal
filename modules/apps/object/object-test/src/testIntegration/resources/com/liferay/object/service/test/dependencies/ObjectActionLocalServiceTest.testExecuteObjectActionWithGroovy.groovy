import com.liferay.portal.kernel.json.JSONObject
import com.liferay.portal.kernel.log.Log
import com.liferay.portal.kernel.log.LogFactoryUtil
import com.liferay.portal.kernel.test.util.HTTPTestUtil
import com.liferay.portal.kernel.util.Http

Log log = LogFactoryUtil.getLog("GROOVY_OBJECT_ACTION");

JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(null, restContextPath + "/" + id, Http.Method.GET);

log.info("Object entry ID " + jsonObject.getLong("id"));