<?xml version="1.0"?>
<!DOCTYPE resource-action-mapping PUBLIC "-//Liferay//DTD Resource Action Mapping 7.4.0//EN" "http://www.liferay.com/dtd/liferay-resource-action-mapping_7_4_0.dtd">

<resource-action-mapping>
	<portlet-resource>
		<portlet-name>[$PORTLET_NAME$]</portlet-name>
		<permissions>
			<supports>
				<action-key>ACCESS_IN_CONTROL_PANEL</action-key>
				<action-key>ADD_TO_PAGE</action-key>
				<action-key>CONFIGURATION</action-key>
			</supports>
			<guest-unsupported>
				<action-key>CONFIGURATION</action-key>
				<action-key>PERMISSIONS</action-key>
			</guest-unsupported>
		</permissions>
	</portlet-resource>
	<model-resource>
		<model-name>[$RESOURCE_NAME$]</model-name>
		<portlet-ref>
			<portlet-name>[$PORTLET_NAME$]</portlet-name>
		</portlet-ref>
		<root>true</root>
		<weight>1</weight>
		<permissions>
			<supports>
				<action-key>ADD_OBJECT_ENTRY</action-key>
				<action-key>DELETE</action-key>
				<action-key>PERMISSIONS</action-key>
				<action-key>UPDATE</action-key>
				<action-key>VIEW</action-key>
			</supports>
			<guest-defaults />
			<guest-unsupported>
				<action-key>PERMISSIONS</action-key>
			</guest-unsupported>
		</permissions>
	</model-resource>
</resource-action-mapping>