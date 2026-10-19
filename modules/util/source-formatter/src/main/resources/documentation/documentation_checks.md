# Documentation Checks

Check | File Extensions | Description
----- | --------------- | -----------
JavaSourceFormatterDocumentationCheck | .java | Finds SourceFormatter checks that have no documentation. |
LFRBuildReadmeCheck | .lfrbuild-* | Checks that `.lfrbuild*` files are documented in a marker file. |
MarkdownSourceFormatterDocumentationCheck | .markdown or .md | Validates the header of Source Formatter documentation files. |
MarkdownSourceFormatterReadmeCheck | .markdown or .md | Generates Source Formatter documentation index files. |
PropertiesEnvironmentVariablesCheck | .eslintignore, .prettierignore, or .properties | Verifies that the environment property in the documentation matches the property name. |