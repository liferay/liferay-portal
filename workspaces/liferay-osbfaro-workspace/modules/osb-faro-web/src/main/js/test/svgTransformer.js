const path = require('path');

/**
 * Webpack emits the favicon SVGs as standalone assets, so importing one yields
 * its URL. Jest has no SVG transform of its own, so stand in for that with the
 * file name, which keeps each import distinguishable in assertions.
 */

module.exports = {
	process(sourceText, sourcePath) {
		return {
			code: `module.exports = ${JSON.stringify(
				path.basename(sourcePath)
			)};`,
		};
	},
};
