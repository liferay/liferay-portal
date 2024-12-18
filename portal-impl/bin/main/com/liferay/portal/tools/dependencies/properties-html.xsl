<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" indent="yes"/>

	<xsl:template match="/">
		<html>
			<style type="text/css">
				h1 {color:red;}
				h2 {color:blue;}
				h3 {color:darkgreen;}
				h4 {color:white; background-color:red;}
				.content {border: 1px dotted #666; padding: 10px;}
				.description {color: #444;}
				.value {background-color: #efe; font-size: 14px; padding: 4px; margin: 10px 20px 20px 20px; font-weight:bold;}
				.alt {font-weight: normal; background-color: #eef;}
				.hidden {background-color: #eee;}
				#toc {color:#555; border: 1px dotted #555; width: 700px;}
				#toc li {font-size:14px;}
				#toc li li {font-size:12px;}
			</style>
			<body>
				<h1>Portal Properties</h1>

				<xsl:call-template name="toc">
					<xsl:with-param name="fullToc"><xsl:value-of select="/params/fullToc" /></xsl:with-param>
				</xsl:call-template>

				<xsl:apply-templates select="properties/section"/>

			</body>
		</html>
	</xsl:template>

	<xsl:template name="toc" >
		<xsl:param name="fullToc" />
		TOC
		<ul id="toc">
		<xsl:for-each select="properties/section">
			<li>
				<a href="#{anchor}"><xsl:value-of select="title" /></a>
				<xsl:if test="$fullToc = 'true'">
				<ul>
					<xsl:for-each select="content/property">
						<li>
							<a href="#{anchor}">
								<xsl:if test="@prefix != ''">
									<xsl:value-of select="@prefix" />.*
								</xsl:if>
								<xsl:if test="@prefix = ''">
									<xsl:value-of select="name" />
								</xsl:if>
							</a>
						</li>
					</xsl:for-each>
				</ul>
				</xsl:if>
			</li>
		</xsl:for-each>
		</ul>
	</xsl:template>


	<xsl:template match="section">

		<h2><a name="{anchor}"/><xsl:value-of select="title" />&#160;<a href="#{anchor}">#</a></h2>

		<div class="content">
			<xsl:apply-templates select="content/property"/>
		</div>

	</xsl:template>


	<!-- group of properties -->
	<xsl:template match="property[@prefix != '']">

		<xsl:variable name="g" select="@group"/>

		<h3><a name="{anchor}"/><xsl:value-of select="@prefix" />.*&#160;<a href="#{anchor}">#</a></h3>
		<div>Properties:
			<ul>
				<xsl:for-each select="../property-group[@group = $g]">
					<li><xsl:value-of select="name" /></li>
				</xsl:for-each>
			</ul>
		</div>

		<div class="description"><xsl:value-of select="description" /></div>

		<xsl:if test="@hidden='false'">
			<div class="value">
				Default values:
				<pre>
					<xsl:apply-templates select="../property-group[@group = $g]"/>
				</pre>
			</div>
		</xsl:if>
		<xsl:if test="@hidden='true'">
			<div class="value hidden">
				Default values (not active):
				<pre>
					<xsl:apply-templates select="../property-group[@group = $g]"/>
				</pre>
			</div>
		</xsl:if>

	</xsl:template>


	<!-- property -->
	<xsl:template match="property[@prefix = '']">
		<h3><a name="{anchor}"/><xsl:value-of select="name" />&#160;<a href="#{anchor}">#</a></h3>

		<div class="description"><xsl:value-of select="description" /></div>

		<xsl:if test="@hidden='false'">
			<div class="value">Default value: <pre><xsl:value-of select="value" /></pre></div>
		</xsl:if>
		<xsl:if test="@hidden='true'">
			<div class="value hidden">Default value (not active): <pre><xsl:value-of select="value" /></pre></div>
		</xsl:if>

		<xsl:if test="count(value[@alt='true']) > 0">
			<div class="value alt">Other sample values:
				<pre>
					<xsl:for-each select="value[@alt='true']">
						<xsl:value-of select="." /><br/>
					</xsl:for-each>
				</pre>
			</div>
		</xsl:if>

	</xsl:template>

	<xsl:template match="property-group">
		<xsl:value-of select="name" />=<xsl:value-of select="value" /><br/>
	</xsl:template>

</xsl:stylesheet>