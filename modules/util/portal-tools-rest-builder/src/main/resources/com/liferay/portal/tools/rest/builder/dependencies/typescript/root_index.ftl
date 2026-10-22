/**
 * @author ${configYAML.author}
 * @generated
 */

<#list escapedVersions?sort as escapedVersion>
	export * as ${escapedVersion} from './${escapedVersion}';
</#list>