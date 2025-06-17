# Source Formatter

## Configuration

### Configuration Files

Checks are configured in the following files:
- [checkstyle.xml](src/main/resources/checkstyle.xml)
- [checkstyle-jsp.xml](src/main/resources/checkstyle-jsp.xml)
- [sourcechecks.xml](src/main/resources/sourcechecks.xml)

### Excluding Files or Directories

1. Exclude a **single file** from **one specific check**

   Use `checkstyle-suppressions.xml`:

   ```
   <suppressions>
       <checkstyle>
           <suppress checks="UnusedVariableCheck" files="portal-kernel/src/com/liferay/portal/kernel/util/PortalUtil\.java" />
       </checkstyle>
       <source-check>
           <suppress checks="JavaModuleIllegalImportsCheck" files="portal-kernel/src/com/liferay/portal/kernel/util/PortalUtil\.java" />
       </source-check>
   </suppressions>
   ```

1. Exclude a **single file** from **all checks**

   Use `source-formatter.properties#source.formatter.excludes`:

   ```
   source.formatter.excludes=portal-kernel/src/com/liferay/portal/kernel/util/PortalUtil.java
   ```

1. Exclude **all files in a directory** from **one specific check**

   Use `checkstyle-suppressions.xml`:

   ```
   <suppressions>
       <checkstyle>
           <suppress checks="UnusedVariableCheck" files="portal-kernel/src/com/liferay/portal/kernel/util/.*" />
       </checkstyle>
       <source-check>
           <suppress checks="JavaModuleIllegalImportsCheck" files="portal-kernel/src/com/liferay/portal/kernel/util/.*" />
       </source-check>
   </suppressions>
   ```

1. Exclude a **all files in a directory** from **all checks**

   - Use `source-formatter.properties#source.formatter.excludes`:

      ```
      source.formatter.excludes=portal-kernel/src/com/liferay/portal/kernel/util/**
      ```

   - Add (empty) file `source_formatter.ignore` in the directory

1. Exclude **all files in the project** from **one specific check**

   - Use `checkstyle-suppressions.xml`:

      ```
      <suppressions>
          <checkstyle>
              <suppress checks="UnusedVariableCheck" />
          </checkstyle>
          <source-check>
              <suppress checks="JavaModuleIllegalImportsCheck" />
          </source-check>
      </suppressions>
      ```

   - Use property `enabled` in `source-formatter.properties`:

   ```
   checkstyle.UnusedVariableCheck.enabled=false
   source.check.JavaModuleIllegalImportsCheck.enabled=false
   ```

## Checks

- ### [All Checks](src/main/resources/documentation/all_checks.md#all-checks)

- ### By Category:
   - [Bug Prevention](src/main/resources/documentation/bug_prevention_checks.md#bug-prevention-checks)
   - [Documentation](src/main/resources/documentation/documentation_checks.md#documentation-checks)
   - [JakartaTransform](src/main/resources/documentation/jakarta_transform_checks.md#jakartatransform-checks)
   - [Javadoc](src/main/resources/documentation/javadoc_checks.md#javadoc-checks)
   - [Miscellaneous](src/main/resources/documentation/miscellaneous_checks.md#miscellaneous-checks)
   - [Naming Conventions](src/main/resources/documentation/naming_conventions_checks.md#naming-conventions-checks)
   - [Performance](src/main/resources/documentation/performance_checks.md#performance-checks)
   - [Productivity](src/main/resources/documentation/productivity_checks.md#productivity-checks)
   - [Security](src/main/resources/documentation/security_checks.md#security-checks)
   - [Styling](src/main/resources/documentation/styling_checks.md#styling-checks)
   - [Upgrade](src/main/resources/documentation/upgrade_checks.md#upgrade-checks)

- ### By File Extensions:
   - [.action, .function, .jelly, .jrxml, .macro, .pom, .project, .properties, .svg, .testcase, .toggle, .tpl, .wsdl, .xml, or .xsd](src/main/resources/documentation/xml_source_processor_checks.md#checks-for-action-function-jelly-jrxml-macro-pom-project-properties-svg-testcase-toggle-tpl-wsdl-xml-or-xsd)
   - [.bnd](src/main/resources/documentation/bnd_source_processor_checks.md#checks-for-bnd)
   - [.bnd, .ftl, .gradle, .java, .json, .jsp, .jspf, .scss, or .vm](src/main/resources/documentation/upgrade_source_processor_checks.md#checks-for-bnd-ftl-gradle-java-json-jsp-jspf-scss-or-vm)
   - [.bndrun](src/main/resources/documentation/bnd_run_source_processor_checks.md#checks-for-bndrun)
   - [.cfg or .config](src/main/resources/documentation/config_source_processor_checks.md#checks-for-cfg-or-config)
   - [.cql](src/main/resources/documentation/cql_source_processor_checks.md#checks-for-cql)
   - [.css or .scss](src/main/resources/documentation/css_source_processor_checks.md#checks-for-css-or-scss)
   - [.dtd](src/main/resources/documentation/dtd_source_processor_checks.md#checks-for-dtd)
   - [.eslintignore, .prettierignore, or .properties](src/main/resources/documentation/properties_source_processor_checks.md#checks-for-eslintignore-prettierignore-or-properties)
   - [.expect or .sh](src/main/resources/documentation/sh_source_processor_checks.md#checks-for-expect-or-sh)
   - [.ftl](src/main/resources/documentation/ftl_source_processor_checks.md#checks-for-ftl)
   - [.ftl, .html, .jsp, .jspf, .jspx, or .vm](src/main/resources/documentation/csp_source_processor_checks.md#checks-for-ftl-html-jsp-jspf-jspx-or-vm)
   - [.function, .jar, .lar, .macro, .path, .testcase, .war, or .zip](src/main/resources/documentation/poshi_source_processor_checks.md#checks-for-function-jar-lar-macro-path-testcase-war-or-zip)
   - [.gitrepo or ci-merge](src/main/resources/documentation/ci_merge_and_git_repo_source_processor_checks.md#checks-for-gitrepo-or-cimerge)
   - [.gradle](src/main/resources/documentation/gradle_source_processor_checks.md#checks-for-gradle)
   - [.gradle, .gradle, .gradle, .gradle, .json, .json, .properties, .properties, .xml, or .xml](src/main/resources/documentation/library_source_processor_checks.md#checks-for-gradle-gradle-gradle-gradle-json-json-properties-properties-xml-or-xml)
   - [.groovy](src/main/resources/documentation/groovy_source_processor_checks.md#checks-for-groovy)
   - [.html or .path](src/main/resources/documentation/html_source_processor_checks.md#checks-for-html-or-path)
   - [.ipynb, .json, or .npmbridgerc](src/main/resources/documentation/json_source_processor_checks.md#checks-for-ipynb-json-or-npmbridgerc)
   - [.java](src/main/resources/documentation/java_source_processor_checks.md#checks-for-java)
   - [.js or .jsx](src/main/resources/documentation/js_source_processor_checks.md#checks-for-js-or-jsx)
   - [.jsp, .jspf, .jspx, .tag, .tpl, or .vm](src/main/resources/documentation/jsp_source_processor_checks.md#checks-for-jsp-jspf-jspx-tag-tpl-or-vm)
   - [.ldif](src/main/resources/documentation/ldif_source_processor_checks.md#checks-for-ldif)
   - [.lfrbuild-*](src/main/resources/documentation/lfr_build_source_processor_checks.md#checks-for-lfrbuild)
   - [.list](src/main/resources/documentation/list_source_processor_checks.md#checks-for-list)
   - [.markdown or .md](src/main/resources/documentation/markdown_source_processor_checks.md#checks-for-markdown-or-md)
   - [.py](src/main/resources/documentation/python_source_processor_checks.md#checks-for-py)
   - [.soy](src/main/resources/documentation/soy_source_processor_checks.md#checks-for-soy)
   - [.sql](src/main/resources/documentation/sql_source_processor_checks.md#checks-for-sql)
   - [.tf](src/main/resources/documentation/tf_source_processor_checks.md#checks-for-tf)
   - [.tld](src/main/resources/documentation/tld_source_processor_checks.md#checks-for-tld)
   - [.tpl, .yaml, or .yml](src/main/resources/documentation/yml_source_processor_checks.md#checks-for-tpl-yaml-or-yml)
   - [.ts or .tsx](src/main/resources/documentation/ts_source_processor_checks.md#checks-for-ts-or-tsx)
   - [.txt](src/main/resources/documentation/txt_source_processor_checks.md#checks-for-txt)
   - [CODEOWNERS](src/main/resources/documentation/codeowners_source_processor_checks.md#checks-for-codeowners)
   - [Dockerfile](src/main/resources/documentation/dockerfile_source_processor_checks.md#checks-for-dockerfile)
   - [packageinfo](src/main/resources/documentation/packageinfo_source_processor_checks.md#checks-for-packageinfo)