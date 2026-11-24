import axe from 'axe-core';

const AXE_OPTIONS = {
	rules: {

		// JSDOM has no layout, so contrast and target size cannot be measured.

		'color-contrast': {enabled: false},
		'target-size': {enabled: false},
	},
	runOnly: ['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa', 'wcag22aa'],
};

/**
 * Runs axe on `container` and returns its WCAG A/AA violations in a readable
 * shape. Selectors in `exclude` are left out, such as components covered by
 * their own tests.
 */
export async function getAccessibilityViolations(
	container,
	{exclude = []} = {}
) {
	jest.useRealTimers();

	try {
		const {violations} = await axe.run(
			{
				exclude: exclude.map((selector) => [selector]),
				include: [container],
			},
			AXE_OPTIONS
		);

		return violations.map(({help, id, nodes}) => ({
			help,
			id,
			targets: nodes.map(({target}) => target.join(' ')),
		}));
	}
	finally {
		jest.useFakeTimers();
	}
}

export async function expectNoAccessibilityViolations(container, options) {
	expect(await getAccessibilityViolations(container, options)).toEqual([]);
}
