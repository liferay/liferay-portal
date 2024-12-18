<div class="portlet-display-template-error">
	<span class="alert alert-error">
		<@liferay.language key="an-error-occurred-while-processing-the-template" />
	</span>

	<pre>${exception?html}</pre>

	<div class="scroll-pane">
		<div class="inner-scroll-pane">
			<#assign lines = stringUtil.split(script, "\n") />

			<#list lines as curLine>
				<#assign css = '' />

				<#if line?exists && line == (curLine_index + 1)>
					<#assign css = ' class="error-line"' />
				</#if>

				<pre${css}><span>${curLine_index + 1}</span>${curLine?html}&nbsp;</pre>
			</#list>
		</div>
	</div>
</div>