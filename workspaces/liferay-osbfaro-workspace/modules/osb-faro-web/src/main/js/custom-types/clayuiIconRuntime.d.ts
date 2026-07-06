/**
 * `@clayui/icon-runtime` is a synthetic specifier that webpack externalizes to
 * the DXP's runtime `@clayui/icon` (resolved through the portal import map). It
 * yields the PORTAL's `ClayIconSpriteContext` instance -- the one
 * `FrontendDataSet` reads -- which is distinct from the bundled `@clayui/icon`
 * context the rest of the app consumes.
 */
declare module '@clayui/icon-runtime' {
	export {ClayIconSpriteContext} from '@clayui/icon';
}
