const path = require('path');

const AC_ICONS_DIR = path.join('src', 'main', 'images');

/**
 * Stands in for the two ways webpack loads an SVG. An Analytics Cloud icon
 * (`src/main/images`) is a React component built by SVGR, so it becomes an
 * `svg` element that names its file. Anything else, such as a favicon, an empty
 * state illustration, or the Clay sprite imported with `?url`, is a URL, so it
 * becomes its file name. In both cases the file name keeps each import
 * distinguishable in assertions.
 */

module.exports = {
	process(sourceText, sourcePath) {
		const fileName = JSON.stringify(path.basename(sourcePath));

		if (path.dirname(sourcePath).endsWith(AC_ICONS_DIR)) {
			return {
				code: `
					const React = require('react');

					const Icon = React.forwardRef((props, ref) =>
						React.createElement('svg', {
							...props,
							'data-file': ${fileName},
							ref,
						})
					);

					module.exports = {__esModule: true, default: Icon};
				`,
			};
		}

		return {
			code: `module.exports = ${fileName};`,
		};
	},
};
