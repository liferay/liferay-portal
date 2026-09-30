# Knowledge Base Article Parent Title 1 [](id=knowledge-base-article-parent-title-1)

A portlet's Display Settings (*Options* &rarr; *Configuration* &rarr; *Setup*
&rarr; *Display Settings*) let you customize its display. They come built in
with Liferay, so you don't have to do anything special to enable them for your
custom portlets. But what if you need settings in addition to Liferay's default
display settings? You could develop a theme or hook with the display options you
need, but it'd be nice if you could apply particular display options to specific
portlet instances without having to redeploy any plugins. Ideally, you should be
able to provide authorized portal users the ability to apply custom display
settings to portlets. This saves you from having to change portlet configuration
code every time you need new settings.

Be of good cheer! That's precisely what
[Application Display Templates](/discover/portal/-/knowledge_base/6-2/using-application-display-templates)
(ADTs) provide--the ability to add custom display settings to your portlets from
the portal. This isn't actually a new concept in Liferay. In some portlets
(e.g., *Web Content*, *Documents and Media*, and *Dynamic Data Lists*), you can
already add as many display options (or templates) as you want. Now you can add
them to your custom portlets, too.