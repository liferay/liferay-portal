/**
 * Fills the icon sprite.
 *
 * `svg-sprite-loader` builds `dist/sprite.svg` out of the SVG files the bundle
 * imports, and `App` points Clay's `ClayIconSpriteContext` at that file. So an
 * icon is only drawable if something in the bundle imported its SVG — and no
 * component imports icons one by one, because `<ClayIcon symbol="x" />` names
 * the symbol as a string that webpack never sees.
 *
 * The two `require.context` calls below are what put every icon in the sprite.
 * They are imported for that side effect alone and export nothing.
 *
 * This file exists because the contexts used to sit in a utility module that
 * happened to be imported by one dashboard. Deleting that dashboard emptied the
 * sprite of every Clay icon, and nothing failed: the build passed, the tests
 * passed, and the UI rendered blank `<use>` elements where its icons had been.
 * Keep the import in `main` and keep this module free of exports, so that what
 * feeds the sprite stays something you can find by name.
 */

require.context('@clayui/css/src/images/icons', false, /\.svg$/);
require.context('../images', false, /\.svg$/);
