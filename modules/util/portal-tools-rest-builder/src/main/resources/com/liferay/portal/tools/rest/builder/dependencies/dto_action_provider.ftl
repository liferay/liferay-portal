package ${configYAML.apiPackagePath}.internal.dto.${escapedVersion}.action;

import ${configYAML.apiPackagePath}.internal.dto.${escapedVersion}.action.metadata.${schemaName}DTOActionMetadataProvider;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.portal.vulcan.dto.action.ActionInfo;
import com.liferay.portal.vulcan.dto.action.DTOActionProvider;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Component(
	<#if configYAML.liferayEnterpriseApp>enabled = false,</#if>
	property = {
		"dto.class.name=${configYAML.apiPackagePath}.dto.${escapedVersion}.${schemaName}",
	},
	service = DTOActionProvider.class
)
@Generated("")
public class ${schemaName}DTOActionProvider implements DTOActionProvider {

	@Override
	public Map<String, ActionInfo> getActionInfos() throws Exception {
		Map<String, ActionInfo> actionInfos = new HashMap<>();

		${schemaName}DTOActionMetadataProvider ${schemaVarName}DTOActionMetadataProvider = new ${schemaName}DTOActionMetadataProvider();

		for (String actionName : ${schemaVarName}DTOActionMetadataProvider.getActionNames()) {
			actionInfos.put(actionName, ${schemaVarName}DTOActionMetadataProvider.getActionInfo(actionName));
		}

		return actionInfos;
	}

	@Override
	public Map<String, Map<String, String>> getActions(long groupId, long primaryKey, UriInfo uriInfo, long userId) {
		Map<String, Map<String, String>> actions = new HashMap<>();

		${schemaName}DTOActionMetadataProvider ${schemaVarName}DTOActionMetadataProvider = new ${schemaName}DTOActionMetadataProvider();

		for (String actionName : ${schemaVarName}DTOActionMetadataProvider.getActionNames()) {
			ActionInfo actionInfo = ${schemaVarName}DTOActionMetadataProvider.getActionInfo(actionName);

			if (actionInfo == null || actionInfo.getActionName() == null || actionInfo.getResourceMethodName() == null) {
				continue;
			}

			actions.put(actionName, ActionUtil.addAction(actionInfo.getActionName(), actionInfo.getResourceClass(), primaryKey, actionInfo.getResourceMethodName(), _scopeChecker,userId, ${schemaVarName}DTOActionMetadataProvider.getPermissionName(), groupId, () -> UriInfoUtil.getBaseUriBuilder("${configYAML.application.baseURI?remove_beginning("/")}", uriInfo), uriInfo));
		}

		return actions;
	}

	@Reference
	private ScopeChecker _scopeChecker;

}