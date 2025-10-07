import com.liferay.object.service.ObjectEntryLocalServiceUtil
import com.liferay.portal.kernel.service.ServiceContext

Map<String, Serializable> values = new HashMap<>();

values.put("integerObjectField", integerObjectField + 1);

ObjectEntryLocalServiceUtil.updateObjectEntry(creator, id, 0, values, new ServiceContext());