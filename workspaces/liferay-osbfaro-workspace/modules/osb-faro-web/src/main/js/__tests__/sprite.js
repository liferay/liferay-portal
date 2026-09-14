import fs from 'fs';
import path from 'path';

const read = (file) =>
	fs.readFileSync(path.join(__dirname, '..', file), 'utf-8');

/**
 * The sprite is filled by a side effect, so nothing that runs in a test can
 * observe it: `require.context` is a webpack construct, and the icons only
 * reach the page through a file the bundler emits. What can be checked is the
 * wiring, which is exactly what broke before — the import went away with the
 * last module that happened to pull it in, and every Clay icon went blank
 * without a single failure anywhere.
 */
describe('sprite', () => {
	it('should be imported by the entry module', () => {
		expect(read('main.jsx')).toContain("import './sprite'");
	});

	it('should pull in both the Clay and the Faro icons', () => {
		const source = read('sprite.js');

		expect(source).toContain(
			"require.context('@clayui/css/src/images/icons'"
		);
		expect(source).toContain("require.context('../images'");
	});

	// Anything exported here invites a caller, and a caller invites someone to
	// decide the import is redundant and drop it.

	it('should export nothing', () => {
		expect(read('sprite.js')).not.toMatch(/^export /m);
	});
});
