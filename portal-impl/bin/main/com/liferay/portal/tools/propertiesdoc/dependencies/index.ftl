<html>
	<head>
		<title>Liferay Portal ${releaseInfoVersion} Properties</title>

		<meta charset="UTF-8" />
	</head>

	<body>
		<p>
			<strong>Liferay Portal ${releaseInfoVersion} Properties</strong>
		</p>

		<p>
			Here is a listing of Liferay Portal ${releaseInfoVersion} properties files and properties definition files:
		</p>

		<ul>
			<#list propertiesHTMLFileNames as propertiesHTMLFileName>
				<li>
					<a href="${propertiesHTMLFileName}.html" title="${propertiesHTMLFileName}.html">${propertiesHTMLFileName}</a>
				</li>
			</#list>
		</ul>
	</body>
</html>