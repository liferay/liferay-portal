// This file was automatically generated from form.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace ddm.
 * @public
 */

if (typeof ddm == 'undefined') { var ddm = {}; }


ddm.field = function(opt_data, opt_ignored) {
  return '' + ((opt_data.field != null) ? soy.$$filterNoAutoescape(opt_data.field) : '');
};
if (goog.DEBUG) {
  ddm.field.soyTemplateName = 'ddm.field';
}


ddm.fields = function(opt_data, opt_ignored) {
  var output = '';
  var fieldList10 = opt_data.fields;
  var fieldListLen10 = fieldList10.length;
  for (var fieldIndex10 = 0; fieldIndex10 < fieldListLen10; fieldIndex10++) {
    var fieldData10 = fieldList10[fieldIndex10];
    output += ddm.field(soy.$$augmentMap(opt_data, {field: fieldData10}));
  }
  return output;
};
if (goog.DEBUG) {
  ddm.fields.soyTemplateName = 'ddm.fields';
}


ddm.form_renderer_js = function(opt_data, opt_ignored) {
  return '<script type="text/javascript">AUI().use( \'liferay-ddm-form-renderer\', \'liferay-ddm-form-renderer-field\', function(A) {Liferay.DDM.Renderer.FieldTypes.register(' + soy.$$filterNoAutoescape(opt_data.fieldTypes) + '); Liferay.component( \'' + soy.$$escapeJsString(opt_data.containerId) + 'DDMForm\', new Liferay.DDM.Renderer.Form({container: \'#' + soy.$$escapeJsString(opt_data.containerId) + '\', dataProviderURL: \'' + soy.$$filterNoAutoescape(opt_data.dataProviderURL) + '\', definition: ' + soy.$$filterNoAutoescape(opt_data.definition) + ', evaluation: ' + soy.$$filterNoAutoescape(opt_data.evaluation) + ', evaluatorURL: \'' + soy.$$filterNoAutoescape(opt_data.evaluatorURL) + '\',' + ((opt_data.layout) ? 'layout: ' + soy.$$filterNoAutoescape(opt_data.layout) + ',' : '') + 'portletNamespace: \'' + soy.$$escapeJsString(opt_data.portletNamespace) + '\', readOnly: ' + soy.$$escapeJsValue(opt_data.readOnly) + ', readOnlyFields : ' + soy.$$filterNoAutoescape(opt_data.readOnlyFields) + ', showRequiredFieldsWarning: ' + soy.$$escapeJsValue(opt_data.showRequiredFieldsWarning) + ', showSubmitButton: ' + soy.$$escapeJsValue(opt_data.showSubmitButton) + ', submitLabel: \'' + soy.$$escapeJsString(opt_data.submitLabel) + '\', templateNamespace: \'' + soy.$$escapeJsString(opt_data.templateNamespace) + '\', values: ' + soy.$$filterNoAutoescape(opt_data.values) + '}).render() );});<\/script>';
};
if (goog.DEBUG) {
  ddm.form_renderer_js.soyTemplateName = 'ddm.form_renderer_js';
}


ddm.form_rows = function(opt_data, opt_ignored) {
  var output = '';
  var rowList63 = opt_data.rows;
  var rowListLen63 = rowList63.length;
  for (var rowIndex63 = 0; rowIndex63 < rowListLen63; rowIndex63++) {
    var rowData63 = rowList63[rowIndex63];
    output += '<div class="row">' + ddm.form_row_columns(soy.$$augmentMap(opt_data, {columns: rowData63.columns})) + '</div>';
  }
  return output;
};
if (goog.DEBUG) {
  ddm.form_rows.soyTemplateName = 'ddm.form_rows';
}


ddm.form_row_column = function(opt_data, opt_ignored) {
  return '<div class="col-md-' + soy.$$escapeHtmlAttribute(opt_data.column.size) + '">' + ddm.fields(soy.$$augmentMap(opt_data, {fields: opt_data.column.fields})) + '</div>';
};
if (goog.DEBUG) {
  ddm.form_row_column.soyTemplateName = 'ddm.form_row_column';
}


ddm.form_row_columns = function(opt_data, opt_ignored) {
  var output = '';
  var columnList75 = opt_data.columns;
  var columnListLen75 = columnList75.length;
  for (var columnIndex75 = 0; columnIndex75 < columnListLen75; columnIndex75++) {
    var columnData75 = columnList75[columnIndex75];
    output += ddm.form_row_column(soy.$$augmentMap(opt_data, {column: columnData75}));
  }
  return output;
};
if (goog.DEBUG) {
  ddm.form_row_columns.soyTemplateName = 'ddm.form_row_columns';
}


ddm.required_warning_message = function(opt_data, opt_ignored) {
  return '' + ((opt_data.showRequiredFieldsWarning) ? soy.$$filterNoAutoescape(opt_data.requiredFieldsWarningMessageHTML) : '');
};
if (goog.DEBUG) {
  ddm.required_warning_message.soyTemplateName = 'ddm.required_warning_message';
}


ddm.paginated_form = function(opt_data, opt_ignored) {
  var output = '<div class="lfr-ddm-form-container" id="' + soy.$$escapeHtmlAttribute(opt_data.containerId) + '"><div class="lfr-ddm-form-content">';
  if (opt_data.pages.length > 1) {
    output += '<div class="lfr-ddm-form-wizard"><ul class="multi-step-progress-bar">';
    var pageList99 = opt_data.pages;
    var pageListLen99 = pageList99.length;
    for (var pageIndex99 = 0; pageIndex99 < pageListLen99; pageIndex99++) {
      var pageData99 = pageList99[pageIndex99];
      output += '<li ' + ((pageIndex99 == 0) ? 'class="active"' : '') + '><div class="progress-bar-title">' + soy.$$filterNoAutoescape(pageData99.title) + '</div><div class="divider"></div><div class="progress-bar-step">' + soy.$$escapeHtml(pageIndex99 + 1) + '</div></li>';
    }
    output += '</ul></div>';
  }
  output += '<div class="lfr-ddm-form-pages" autoescape="deprecated-contextual">';
  var pageList124 = opt_data.pages;
  var pageListLen124 = pageList124.length;
  for (var pageIndex124 = 0; pageIndex124 < pageListLen124; pageIndex124++) {
    var pageData124 = pageList124[pageIndex124];
    output += '<div class="' + ((pageIndex124 == 0) ? 'active' : '') + ' lfr-ddm-form-page">' + ((pageData124.title) ? '<h3 class="lfr-ddm-form-page-title">' + soy.$$escapeHtml(pageData124.title) + '</h3>' : '') + ((pageData124.description) ? '<h4 class="lfr-ddm-form-page-description">' + soy.$$escapeHtml(pageData124.description) + '</h4>' : '') + ddm.required_warning_message(soy.$$augmentMap(opt_data, {showRequiredFieldsWarning: pageData124.showRequiredFieldsWarning, requiredFieldsWarningMessageHTML: opt_data.requiredFieldsWarningMessageHTML})) + ddm.form_rows(soy.$$augmentMap(opt_data, {rows: pageData124.rows})) + '</div>';
  }
  output += '</div></div><div class="lfr-ddm-form-pagination-controls"><button class="btn btn-lg btn-primary hide lfr-ddm-form-pagination-prev" type="button"><i class="icon-angle-left"></i> ' + soy.$$escapeHtml(opt_data.strings.previous) + '</button><button class="btn btn-lg btn-primary' + ((opt_data.pages.length == 1) ? ' hide' : '') + ' lfr-ddm-form-pagination-next pull-right" type="button">' + soy.$$escapeHtml(opt_data.strings.next) + ' <i class="icon-angle-right"></i></button>' + ((opt_data.showSubmitButton) ? '<button class="btn btn-lg btn-primary' + ((opt_data.pages.length > 1) ? ' hide' : '') + ' lfr-ddm-form-submit pull-right" disabled type="submit">' + soy.$$escapeHtml(opt_data.submitLabel) + '</button>' : '') + '</div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.paginated_form.soyTemplateName = 'ddm.paginated_form';
}


ddm.simple_form = function(opt_data, opt_ignored) {
  var output = '<div class="lfr-ddm-form-container" id="' + soy.$$escapeHtmlAttribute(opt_data.containerId) + '"><div class="lfr-ddm-form-fields">';
  var pageList154 = opt_data.pages;
  var pageListLen154 = pageList154.length;
  for (var pageIndex154 = 0; pageIndex154 < pageListLen154; pageIndex154++) {
    var pageData154 = pageList154[pageIndex154];
    output += ddm.required_warning_message(soy.$$augmentMap(opt_data, {showRequiredFieldsWarning: pageData154.showRequiredFieldsWarning, requiredFieldsWarningMessageHTML: opt_data.requiredFieldsWarningMessageHTML})) + ddm.form_rows(soy.$$augmentMap(opt_data, {rows: pageData154.rows}));
  }
  output += '</div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.simple_form.soyTemplateName = 'ddm.simple_form';
}


ddm.tabbed_form = function(opt_data, opt_ignored) {
  var output = '<div class="lfr-ddm-form-container" id="' + soy.$$escapeHtmlAttribute(opt_data.containerId) + '"><div class="lfr-ddm-form-tabs"><ul class="nav nav-tabs nav-tabs-default">';
  var pageList164 = opt_data.pages;
  var pageListLen164 = pageList164.length;
  for (var pageIndex164 = 0; pageIndex164 < pageListLen164; pageIndex164++) {
    var pageData164 = pageList164[pageIndex164];
    output += '<li><a href="javascript:;">' + soy.$$escapeHtml(pageData164.title) + '</a></li>';
  }
  output += '</ul><div class="tab-content">';
  var pageList178 = opt_data.pages;
  var pageListLen178 = pageList178.length;
  for (var pageIndex178 = 0; pageIndex178 < pageListLen178; pageIndex178++) {
    var pageData178 = pageList178[pageIndex178];
    output += ddm.required_warning_message(soy.$$augmentMap(opt_data, {showRequiredFieldsWarning: pageData178.showRequiredFieldsWarning, requiredFieldsWarningMessageHTML: opt_data.requiredFieldsWarningMessageHTML})) + '<div class="tab-pane ' + ((pageIndex178 == 0) ? 'active' : '') + '">' + ddm.form_rows(soy.$$augmentMap(opt_data, {rows: pageData178.rows})) + '</div>';
  }
  output += '</div></div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.tabbed_form.soyTemplateName = 'ddm.tabbed_form';
}


ddm.settings_form = function(opt_data, opt_ignored) {
  var output = '<div class="lfr-ddm-form-container" id="' + soy.$$escapeHtmlAttribute(opt_data.containerId) + '"><div class="lfr-ddm-settings-form">';
  var pageList196 = opt_data.pages;
  var pageListLen196 = pageList196.length;
  for (var pageIndex196 = 0; pageIndex196 < pageListLen196; pageIndex196++) {
    var pageData196 = pageList196[pageIndex196];
    output += '<div class="lfr-ddm-form-page' + ((pageIndex196 == 0) ? ' active basic' : '') + ((pageIndex196 == pageListLen196 - 1) ? ' advanced' : '') + '">' + ddm.form_rows(soy.$$augmentMap(opt_data, {rows: pageData196.rows})) + '</div>';
  }
  output += '</div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.settings_form.soyTemplateName = 'ddm.settings_form';
}
