def artifact_values($configuration; $infrastructure_provider_chart_name; $liferay_chart_name):
	$configuration.artifacts as $artifacts
	| def field($type; $name; $key):
		$artifacts[$type][$name][$key] // error("artifacts.\($type).\($name).\($key) is missing");
	def chart($name):
		{
			repoURL: "oci://\(field("charts"; $name; "registry"))/\($name)",
			targetRevision: field("charts"; $name; "version")
		};
	def image($name):
		{
			repository: "\(field("images"; $name; "registry"))/\($name)",
			tag: field("images"; $name; "version")
		};
	{
		platformComponents: (chart("liferay-platform-components") + {
			values: {
				infrastructure: chart("liferay-infrastructure"),
				infrastructureProvider: chart($infrastructure_provider_chart_name),
				liferay: chart($liferay_chart_name),
				observability: chart("observability"),
				operatorApplications: {
					dxpOperator: (chart("liferay-dxp-operator") + {
						values: {
							image: image("liferay-dxp-operator")
						}
					})
				}
			}
		})
	};

def platform_values($configuration; $infrastructure_provider_chart_name; $liferay_chart_name):
	.
	* artifact_values($configuration; $infrastructure_provider_chart_name; $liferay_chart_name)
	* {
		platformComponents: {
			values: ($configuration.platformComponents.values // {})
		}
	};