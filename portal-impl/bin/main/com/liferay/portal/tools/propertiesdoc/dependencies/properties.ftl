<html>
	<style type="text/css">
		h1 {
			color: #31548F;
		}

		h2 {
			color: #68708B;
		}

		h3 {
			color: #989DA0;
		}

		#toc {
			border: 1px solid #555;
			color: #555;
			padding: 20px;
			width: 700px;
		}

		#toc li {
			font-size: 14px;
		}

		.defaults {
			background-color: #8598B9;
		}

		.examples {
			background-color: #E4ECF0;
		}
	</style>

	<head>
		<title>${pageTitle!"Portal Properties"}</title>

		<meta charset="UTF-8" />
	</head>

	<body>
		<h1>${pageTitle!"Portal Properties"}</h1>

		<#if toc>
			<h2>Table of Contents</h2>

			<ul id="toc">
				<#list sections as section>
					<#if section.hasTitle()>
			 			<li>
			 				<a href="${propertiesFileName}.html#${section.title}">${section.title}</a>
			 			</li>
			 		</#if>
				</#list>
			</ul>
		</#if>

		<h2>Properties</h2>

		<#list sections as section>
			<#if section.hasTitle()>
				<hr />

				<a name="${section.title}"></a><a href="${propertiesFileName}.html">Top of Page</a>

				<h3>${section.title}</h3>
			</#if>

			<#if section.hasComments()>
				<#list section.comments as paragraph>
					<p>${paragraph}</p>
				</#list>
			</#if>

			<#if section.hasPropertyComments()>
				<hr />

				<#list section.propertyComments as paragraph>
					<#if paragraph.isPreformatted()>
						<p><pre>${paragraph.comment}</pre></p>
					<#else>
						<p>${paragraph.comment}</p>
					</#if>
				</#list>
			</#if>

			<#if section.hasDefaultProperties()>
				<em>Defaults:</em>

				<p><pre class="defaults">${section.defaultProperties}</pre></p>
			</#if>

			<#if section.hasExampleProperties()>
				<em>Examples:</em>

				<p><pre class="examples">${section.exampleProperties}</pre></p>
			</#if>
		</#list>
	</body>
</html>