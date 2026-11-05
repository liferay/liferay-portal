# Change Log

All notable changes to Clay packages are documented in this file.

# [3.167.0] (2026-11-05)

## @clayui/alert

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-66970 Move useAutoClose to its own file ([367ed06](https://github.com/liferay/liferay-portal/commit/367ed067000e8c9e8437a7949e9464ea24e82f79))
- LPD-66970 Simplify useAutoClose ([4f97960](https://github.com/liferay/liferay-portal/commit/4f979603d6e12d0a264946db673b371edbc9c7a3))
- LPD-66970 Avoid starting a second auto close timer ([df929e2](https://github.com/liferay/liferay-portal/commit/df929e29f1eb8efd8439aad6a4b252d40dd2f874))
- LPD-66970 Forget the auto close timer once it fires ([8b227ed](https://github.com/liferay/liferay-portal/commit/8b227eda29a3af521f919bd2f15e44e66924275f))
- LPD-66970 Compute the remaining auto close time from the elapsed time ([2e46482](https://github.com/liferay/liferay-portal/commit/2e46482993b2f71a67b4fca57adc8ea5ec4e2c21))
- LPD-66970 Keep alerts open when the Provider sets persistentAlerts ([1d387a6](https://github.com/liferay/liferay-portal/commit/1d387a6eb1e03b925a0d1d154406ccd8eb13f010))
- LPD-66970 Keep a paused alert open when the Provider flag changes ([47681dd](https://github.com/liferay/liferay-portal/commit/47681dda7a0ae45879ca2d5a26d06ab2e36bf20e))
- LPD-66970 Stop or restart the auto close timer when autoClose changes ([d4db521](https://github.com/liferay/liferay-portal/commit/d4db52184565f902a1e8ad59778eb3dde2989fc9))
- LPD-66970 Call the latest onClose when an alert auto closes ([61f6ff6](https://github.com/liferay/liferay-portal/commit/61f6ff616b15031ad3765d2b3b3e7ed726ac4cb7))
- LPD-66970 Keep alerts open while body has c-prefers-persistent-alerts ([6b4c8c3](https://github.com/liferay/liferay-portal/commit/6b4c8c37bec10fe56e572149de92d42fc007525d))
- LPD-66970 Add a story for a toast that stays open with persistentAlerts ([78de07b](https://github.com/liferay/liferay-portal/commit/78de07bf89a738118608ca450a0606140dce622e))
- LPD-66970 Document persistentAlerts in the Provider and Alert docs ([8a152da](https://github.com/liferay/liferay-portal/commit/8a152daa27a019b96771100b41c92554fa3a6f79))
- LPD-66970 Test alert auto close with external override ([574efa4](https://github.com/liferay/liferay-portal/commit/574efa4945bb8fa4283ffe817668d7b63066d4ce))
- LPD-66971 Move ClayAlert into Alert.tsx for the docs API Reference ([f08b9b6](https://github.com/liferay/liferay-portal/commit/f08b9b6f7706c3a0cbb5dd0f7a3232e387d8ee01))
- LPD-66971 Extract usePrefersPersistentAlerts from useAutoClose ([7dd485d](https://github.com/liferay/liferay-portal/commit/7dd485d2d7151f7d847567684a6ed882180e31be))
- LPD-66971 Export usePrefersPersistentAlerts from @clayui/alert ([978780a](https://github.com/liferay/liferay-portal/commit/978780ae9355cfc1c2f10e3e049b57ff4a9e4c2a))
- LPD-66971 Document usePrefersPersistentAlerts ([32acffa](https://github.com/liferay/liferay-portal/commit/32acffaca61e1c03e3857919c6e378f9b79fc357))
- LPD-66971 Test usePrefersPersistentAlerts ([1748d44](https://github.com/liferay/liferay-portal/commit/1748d442b9eb476f871499463d312d26ecd3d3eb))
- LPD-66971 Remove the redundant ClayAlert auto close test ([387df0a](https://github.com/liferay/liferay-portal/commit/387df0adefba70b11683d3269dbe007045921572))
- LPD-109681 Keep alert content mounted when the alert re-renders ([74199df](https://github.com/liferay/liferay-portal/commit/74199dfc754a059667275291d54bec71b5a0cac8))
- LPD-109454 Keep consumer mouse handlers on ClayAlert ([8b124c9](https://github.com/liferay/liferay-portal/commit/8b124c9d18514971c80ac45159307de0483a6e55))
- LPD-109454 Pause ClayAlert autoClose while focus is inside it ([f588a29](https://github.com/liferay/liferay-portal/commit/f588a29b17a7af1bd9f3c4f9ea28e8f35cd3b1d9))
- LPD-109454 Let hover and focus pause ClayAlert autoClose independently ([99c7c3d](https://github.com/liferay/liferay-portal/commit/99c7c3d55c185f64d5d8c839d04c85b520c61e5a))
- LPD-109454 Document how ClayAlert auto close works ([0192146](https://github.com/liferay/liferay-portal/commit/01921466fa9d66b4ea688cdc0685af25e5f6aa10))
- LPD-109454 Test ClayAlert autoClose pause on focus ([43cf56e](https://github.com/liferay/liferay-portal/commit/43cf56e7ea62f236517e720e20af6ceee1132cda))
- LPD-109681 Type the close event that ClayAlert passes to onClose ([2a9cee6](https://github.com/liferay/liferay-portal/commit/2a9cee6cfa04fc341a663ddef6d04f77cbab9148))
- LPD-109681 Focus the alert when it is shown with autoFocus ([4cd2f3a](https://github.com/liferay/liferay-portal/commit/4cd2f3aa8c533c997ba584518108c8f31356b574))
- LPD-109681 Keep the list of toasts in a Clay toast store ([c6602d4](https://github.com/liferay/liferay-portal/commit/c6602d4ea05ffefd7664d88b38f3e00f5b2eeae4))
- LPD-109681 Document the Toaster, toast stacking and autoFocus ([5d3483c](https://github.com/liferay/liferay-portal/commit/5d3483ce0bc7e0cf196f5884477be0055ffd7668))
- LPD-109681 Add tests for the Clay toast store, Toaster and alert focus ([8c574f7](https://github.com/liferay/liferay-portal/commit/8c574f702d4d3599685c91cb3b942c6863a88d41))
- LPD-109681 Fix prose style violations in alert documentation ([0ff9ad9](https://github.com/liferay/liferay-portal/commit/0ff9ad99b450b84fee65ec5183488a1c570cf654))
- LPD-109681 Fix heading casing in alert documentation ([f201115](https://github.com/liferay/liferay-portal/commit/f201115b3b4ea26db4ef91ab114b5075d474af14))

## @clayui/autocomplete

### Commits

- LPD-100943 Align 'fuzzy' versions ([38131f3](https://github.com/liferay/liferay-portal/commit/38131f3273717a498d1e3c877bc70d9c986521d2))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-101956 Add a caption slot to the Autocomplete menu ([2bd1b80](https://github.com/liferay/liferay-portal/commit/2bd1b80afb21339aaa93a61b9fa0a7d12d29dd37))
- LPD-101956 Test the Autocomplete caption ([5e1f14a](https://github.com/liferay/liferay-portal/commit/5e1f14add9ebc6ab4c61a7383c3501bdab2e5a37))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))

## @clayui/badge

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/breadcrumb

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))

## @clayui/button

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))

## @clayui/card

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-105817 Let a card opt out of the template type classes ([ab7bec9](https://github.com/liferay/liferay-portal/commit/ab7bec936d5e7b4973a939cfbcad31f64c982953))
- LPD-105817 Reuse the navigation card in the applications menu and asset library cards ([f3e589d](https://github.com/liferay/liferay-portal/commit/f3e589d53fc951aa6c256147f6bf9743c8748e68))
- LPD-105817 @clayui/card update tests ([1f46218](https://github.com/liferay/liferay-portal/commit/1f462188f50aef3b5f9f13a5c6c416f74dbba3cb))

## @clayui/color-picker

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-96942 @clayui/color-picker should support light-dark function ([eb3c8dd](https://github.com/liferay/liferay-portal/commit/eb3c8ddf27e9a45b179d0d791ea8ac2c919cf43d))
- LPD-96942 @clayui/color-picker Add test for light-dark values ([48320f5](https://github.com/liferay/liferay-portal/commit/48320f54b2741c526ae6b7869299547a80be19c8))
- LPD-102719 Make the color picker's saturation and brightness map operable ([7ebd4e6](https://github.com/liferay/liferay-portal/commit/7ebd4e63dc91d98e68221be4b544ff0a396a9eff))
- LPD-102719 SF ([a99dd9c](https://github.com/liferay/liferay-portal/commit/a99dd9cbee34b8bdacd92299914ee09190c2b2d9))
- LPD-102719 Assert on the color that changes and refresh the snapshot ([370d210](https://github.com/liferay/liferay-portal/commit/370d2105185f8be59f37ba04ab64edd90f379bb7))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))

## @clayui/core

### Commits

- LPD-100943 Align 'react-transition-group' versions ([f154bb6](https://github.com/liferay/liferay-portal/commit/f154bb645cb315667f4369660b3598e47cd7d4a6))
- LPD-100943 Align 'fuzzy' versions ([38131f3](https://github.com/liferay/liferay-portal/commit/38131f3273717a498d1e3c877bc70d9c986521d2))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-100175 Honor an aria-describedby prop on the vertical nav item ([189799b](https://github.com/liferay/liferay-portal/commit/189799b72db6bcf4a40abd159150a903f2dc6c6b))
- LPD-106891 Keep disabled Picker options reachable by keyboard ([a041b83](https://github.com/liferay/liferay-portal/commit/a041b832f3a6d34941d9a4f39dbf4f7eaa509245))
- LPD-106891 Reset the Picker highlight when closing on a disabled option ([d761e40](https://github.com/liferay/liferay-portal/commit/d761e40d8d7c29385e2224409c6d409a05b07de3))
- LPD-106891 Add a Picker story with disabled options ([be2adfa](https://github.com/liferay/liferay-portal/commit/be2adfac7ce8f296410f47a4deeddc0181f21b57))
- LPD-106891 Test keyboard navigation over disabled Picker options ([d40cfa0](https://github.com/liferay/liferay-portal/commit/d40cfa0d17de6b57313f46121a2504eb82f26bf7))
- LPD-106912 Show the active sort direction on sortable table columns ([f30bdf4](https://github.com/liferay/liferay-portal/commit/f30bdf4d8c312564f961108796c0527c2a6bad10))
- LPD-106912 Test the sort direction indicator on table columns ([960c322](https://github.com/liferay/liferay-portal/commit/960c322a4aeb54a8b47415bdd738f76518761448))
- LPD-106214 Announce the column name on data set sort buttons ([f278c6e](https://github.com/liferay/liferay-portal/commit/f278c6e659ad5db909ab1ecc52f34b4cf0c0fafd))
- LPD-106214 Show the column name in the sort button tooltip ([7c1884d](https://github.com/liferay/liferay-portal/commit/7c1884d573435c88472ce442a547375e51aa49a7))
- LPD-107364 Keep visually hidden content from adding a page scrollbar ([5b9d496](https://github.com/liferay/liferay-portal/commit/5b9d4962df24bbb3de22dbbfd16ef226df04e056))
- LPD-107364 Add tests ([9135f93](https://github.com/liferay/liferay-portal/commit/9135f931e2421199cd2408e3b5f07e78e54397a6))
- LPD-107364 Limit the fixed position to the live announcer ([8d35cc0](https://github.com/liferay/liferay-portal/commit/8d35cc05c5a497e8fb42ea1640b006e3e3e80088))
- LPD-107364 Add tests ([534e5ba](https://github.com/liferay/liferay-portal/commit/534e5ba9eb05e3ccdb14d9f059cefeac4a6d856a))
- LPD-106216 Keep the dropdown menu anchored to its trigger when it fits neither below nor above ([21de791](https://github.com/liferay/liferay-portal/commit/21de791529bd7af6fa605ca6a64805116ccb66c4))
- LPD-111385 Match the SidePanel transition timeouts to the slideout transition durations ([0ef0853](https://github.com/liferay/liferay-portal/commit/0ef085351a0956fa11ae7d92d092792daf2aaf2d))

## @clayui/css

### Commits

- LPD-100943 Align 'sass' versions ([b2e1a20](https://github.com/liferay/liferay-portal/commit/b2e1a20f2843dce1e67bfc5a7ed3da4af70f7d9b))
- LPD-98930 Update border-radius values to match Lexicon ([62f6815](https://github.com/liferay/liferay-portal/commit/62f68158cc4dce5176427bcc3f2873cf8e088660))
- LPD-92565 Create importable files for atlas-custom-properties just like we do for atlas ([df0fcc7](https://github.com/liferay/liferay-portal/commit/df0fcc7f910487bb4c93324b99433d26a26c18a4))
- Revert "LPD-92565 Create importable files for atlas-custom-properties just like we do for atlas" ([d424130](https://github.com/liferay/liferay-portal/commit/d4241309c8c10565ead13231e57f658481611d46))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-102793 @clayui/css InputGroupAI help text should be purple ([963c316](https://github.com/liferay/liferay-portal/commit/963c3160f39080f22de208dc5ed17a860e2db454))
- LPD-102713 @clayui/css add flip-horizontal.svg and flip-vertical.svg ([7bad589](https://github.com/liferay/liferay-portal/commit/7bad5891646f4421edc21e258748a65f95f8b06b))
- LPD-92565 Create importable files for atlas-custom-properties just like we do for atlas ([88fd18e](https://github.com/liferay/liferay-portal/commit/88fd18e06d904f99afa8c636f5f0e7cf186b50ed))
- LPD-101689 @clayui/css Normalize cadmin dropdown and atlas-custom-properties dropdown with Lexicon ([0176bb1](https://github.com/liferay/liferay-portal/commit/0176bb1d4c79d87c64bed907f2dde47088d5c4e9))
- LPD-96939 @clayui/css Add scrollbar tokens to the dark mode ramps ([5b8cb18](https://github.com/liferay/liferay-portal/commit/5b8cb181687ec1f0184c4a2a1964ab04a41857dc))
- LPD-96939 @clayui/css Paint scrollbars under the dark color schemes ([4c59119](https://github.com/liferay/liferay-portal/commit/4c5911977adeb136b262b8f3865ee396b004746d))
- LPD-96939 @clayui/css Add the scrollbar Sass variables ([da03dbd](https://github.com/liferay/liferay-portal/commit/da03dbd93464eb1482406a9617049f9fe0a66c81))
- LPD-101365 @clayui/css fix invalid css output ([491aee3](https://github.com/liferay/liferay-portal/commit/491aee34e4b7964e80565389340303cdef84a96e))
- LPD-101956 Add a caption slot to the Autocomplete menu ([2bd1b80](https://github.com/liferay/liferay-portal/commit/2bd1b80afb21339aaa93a61b9fa0a7d12d29dd37))
- LPD-103935 Give tooltips and popovers a position independent width ([55f2abd](https://github.com/liferay/liferay-portal/commit/55f2abd2b89bdc29ec47db67ab0de70a434eaeab))
- LPD-106015 @clayui/css fix @supports Sass error in older versions ([c7db09e](https://github.com/liferay/liferay-portal/commit/c7db09e1120f7d713686c73f62d2078fba9f118f))
- LPD-106598 @clayui/css add spacing-all.svg, spacing-bottom.svg, spacing-horizontal.svg, spacing-left.svg, spacing-right.svg, spacing-top.svg and spacing-vertical.svg ([5e1d674](https://github.com/liferay/liferay-portal/commit/5e1d67497593efff3af8431363905f97046a1ed0))
- LPD-100174 Paint scrollbars in light mode to match dark ([6879a96](https://github.com/liferay/liferay-portal/commit/6879a9682aea713cc6ac1ae7dfb747ba710597dd))
- LPD-107024 @clayui/css modals use modal-banner-img instead of modal-body aspect-ratio for flush images ([def957f](https://github.com/liferay/liferay-portal/commit/def957fef8920854534ca821cb6afa47ef109e1e))
- LPD-107024 Sort modal-banner-img map entries ([15106ed](https://github.com/liferay/liferay-portal/commit/15106ed7a40a98bf12d4d2cae9dc9ae6462b8780))
- LPD-106891 Show the focus ring on a highlighted disabled Picker option ([33230f8](https://github.com/liferay/liferay-portal/commit/33230f804362a40fd850c5e36ecb6ad16b0802ed))
- LPD-106912 Show the active sort direction on sortable table columns ([f30bdf4](https://github.com/liferay/liferay-portal/commit/f30bdf4d8c312564f961108796c0527c2a6bad10))
- LPD-106912 Color the sorted column's arrow as well as its label ([1a79079](https://github.com/liferay/liferay-portal/commit/1a7907908c325ef6295769bb498c2340f1c2561a))
- LPD-107074 Stop centered modals from scrolling with the backdrop ([535cc33](https://github.com/liferay/liferay-portal/commit/535cc33edef77bf07ccdd4184ec6043adf145222))
- LPD-107074 Sort ([217f275](https://github.com/liferay/liferay-portal/commit/217f275319e6bd26e86a8e6423b34b0cb7d7644d))
- LPD-105817 Reuse the navigation card in the applications menu and asset library cards ([f3e589d](https://github.com/liferay/liferay-portal/commit/f3e589d53fc951aa6c256147f6bf9743c8748e68))
- LPD-105817 @clayui/css add navigation-card and navigation-card-horizontal ([f4080a9](https://github.com/liferay/liferay-portal/commit/f4080a9e3a32940d5865f9fd61ad31cbcb883868))
- LPD-106264 Outline the Clay range thumb so it stays visible in dark mode ([411969e](https://github.com/liferay/liferay-portal/commit/411969e570fc2faf304085b4e70a9d7066d191ca))
- LPD-107549 @clayui/css Size alert indicator icons like Atlas ([c948da9](https://github.com/liferay/liferay-portal/commit/c948da98cb187a90673a0b8be68673ff80a53887))
- LPD-106659 @clayui/css remove duplicate ruleset c-empty-state-sm ([4cd885b](https://github.com/liferay/liferay-portal/commit/4cd885b2e70c2d74f1ae3f78199097aa1a08732b))
- LPD-108035 @clayui/css Alerts and Modals remove remaining Sass math ([4cafe49](https://github.com/liferay/liferay-portal/commit/4cafe491e771d8b9da9dc5c539652f0205a0d1e4))
- LPD-108199 Remove unused sassdoc dependency from clay-css package ([1ff177d](https://github.com/liferay/liferay-portal/commit/1ff177d5de9994604bd3c1a866eee5278b6d476e))
- LPD-108199 SF ([bbcf752](https://github.com/liferay/liferay-portal/commit/bbcf7521cc88e30db20bc88cb74c663e575afca0))
- LPD-108199 Upgrade jsdom for clay-css ([f3f5a1d](https://github.com/liferay/liferay-portal/commit/f3f5a1ddcc16cc4441e1bb81f95ba4577be6f72a))
- LPD-108349 Keep the enlarged focus ring frame under reduced motion ([6440354](https://github.com/liferay/liferay-portal/commit/644035475925923788c3da0d4a03b990911516d8))
- LPD-105063 @clayui/css Add empty_states.svg spritemap and make it availble to consume ([3aa741a](https://github.com/liferay/liferay-portal/commit/3aa741ac3949316e937dc19538cdfae8cd3760ff))
- LPD-105063 @clayui/css add reduced motion styles for empty-states.svg and add c-empty-state-icon ([41b37fe](https://github.com/liferay/liferay-portal/commit/41b37fe9f9ffea39bc164cb4f637a59815e26d19))
- LPD-108598 Apply the dark palettes only when the page asks ([65aa404](https://github.com/liferay/liferay-portal/commit/65aa40495b6ec5192536278065553e40018e7ee1))
- LPD-109573 Make extra small monospaced admin buttons 24px wide ([3d45ff9](https://github.com/liferay/liferay-portal/commit/3d45ff9229ed3a7ee07a355d839c0ed826b8a86e))
- LPD-108836 Close a contextual submenu when the pointer leaves ([2cd229e](https://github.com/liferay/liferay-portal/commit/2cd229eeecde48ac33f93c3b201ecc94fed4ff78))
- LPD-61332 Apply the dark high contrast palette from data-prefers-contrast ([a34953e](https://github.com/liferay/liferay-portal/commit/a34953e9c1f9e96549544a0add532ee6bdb6c42d))
- LPD-96940 @clayui/css Scope empty state svg to c-empty-state-illustration ([6864d3c](https://github.com/liferay/liferay-portal/commit/6864d3c1952cd9ecdd904170f2fe588e98b5dc79))
- LPD-108865 Apply a light high contrast palette on light pages ([d72010c](https://github.com/liferay/liferay-portal/commit/d72010c51c8d6f2d6aaceac278b268c52c671ad1))
- LPD-108865 Remove the unused dark high contrast palette maps ([712189d](https://github.com/liferay/liferay-portal/commit/712189dcfcca7d16b6e8faf5edb04e283b817d97))
- LPD-109791 @clayui/css c-empty-state-illustration should contain the svg in the layout ([1cf61c1](https://github.com/liferay/liferay-portal/commit/1cf61c17b4dfc3c28c7e8623d15277d7de405b0a))
- LPD-109791 @clayui/css fix size of empty state svg ([fd71024](https://github.com/liferay/liferay-portal/commit/fd710248683f6dfa61c7137889400501fa67cc70))
- LPD-108865 Update the high contrast light yellow values ([77257f1](https://github.com/liferay/liferay-portal/commit/77257f1a831ef4f0a063169808595c0e3beb3a38))
- LPD-108570 @clayui/css add product.svg ([4958643](https://github.com/liferay/liferay-portal/commit/4958643186c7e71378f3943cd3ed44c3615c4f2e))
- LPD-108570 SF ([bfc980b](https://github.com/liferay/liferay-portal/commit/bfc980b441204e4c65760d8cbc4013947ac53ef7))
- LPD-90825 Apply the high contrast palette when forced colors are active ([220d4eb](https://github.com/liferay/liferay-portal/commit/220d4eb57fbe04a2906387a121d6fae06e57a297))
- LPD-109681 Stack toasts in fixed toast columns by count ([8915c7a](https://github.com/liferay/liferay-portal/commit/8915c7adefd9832365130799265a30109d9f0fe7))
- LPD-109681 Center inline toasts in the stacked toast pile ([e807336](https://github.com/liferay/liferay-portal/commit/e807336b0692a181f0da170a38f2aee29fc8a82f))
- LPD-112203 Pass clicks through the empty part of the toast container ([4a8d43f](https://github.com/liferay/liferay-portal/commit/4a8d43fa6141e46cd2bd02a1082ce978dc5da4b9))
- LPD-110662 @clayui/css Convert c-slideout-transition-* and c-slideout-sidebar-width to use CSS variable ([838b47a](https://github.com/liferay/liferay-portal/commit/838b47a33ea9d5771278c17d9c02484a81d5a212))

## @clayui/data-provider

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))

## @clayui/date-picker

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-100943 Align 'date-fns' versions ([c4e02a2](https://github.com/liferay/liferay-portal/commit/c4e02a26746bf77592bd56f6e5d760cfa5a60e81))

## @clayui/drop-down

### Commits

- LPD-100943 Align 'react-transition-group' versions ([f154bb6](https://github.com/liferay/liferay-portal/commit/f154bb645cb315667f4369660b3598e47cd7d4a6))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))
- LPD-110929 Add a headerContent slot to ClayDropDownWithItems ([9a49841](https://github.com/liferay/liferay-portal/commit/9a498414db284eaa93e2ed883988d6e1140f5702))

## @clayui/empty-state

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-105063 @clayui/empty-state allow rendering element in place of img tag ([127e646](https://github.com/liferay/liferay-portal/commit/127e646b8afa5e652e4b5b37eb4e595e4b860a0f))
- LPD-105063 @clayui/empty-state storybook add example using empty-states.svg and update tests ([4e62649](https://github.com/liferay/liferay-portal/commit/4e62649ab7ea4b3799fe9dbfc91d7cce7a0afcd5))
- LPD-105063 Render c-empty-state-illustration only when image is set ([a17b433](https://github.com/liferay/liferay-portal/commit/a17b4338890ebceb62b3290001d94094080ae114))

## @clayui/form

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-102793 @clayui/form add suggestion to InputGroupAI ([159fb4d](https://github.com/liferay/liferay-portal/commit/159fb4d2753e26e99f31fbd47d6a6faec3e29991))
- LPD-102793 @clayui/form update InputGroupAI to use suggestion ([04bfd8b](https://github.com/liferay/liferay-portal/commit/04bfd8bef6762c0f68f2710ac4609416ec8a9fe1))
- LPD-102793 @clayui/form update InputGroupAI test ([b7e4c27](https://github.com/liferay/liferay-portal/commit/b7e4c2772d2a5343eada64232e6d067c937c66d7))

## @clayui/icon

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-102713 clayui.com add aliases for flip-horizontal and flip-vertical icons ([f6e0621](https://github.com/liferay/liferay-portal/commit/f6e0621e1955016dba590a51f3b68ed367cda6fc))
- LPD-106598 clayui.com add aliases for spacing-all, spacing-bottom, spacing-horizontal, spacing-left, spacing-right, spacing-top and spacing-vertical icons ([4a5ef13](https://github.com/liferay/liferay-portal/commit/4a5ef133aae9131466a4e2edaffa8cf6f61dd671))
- LPD-105063 @clayui/icon add option to omit lexicon-icon-* classes ([f4509a2](https://github.com/liferay/liferay-portal/commit/f4509a237dd9e62d8e0c03ba89bd855699e49610))
- LPD-105063 @clayui/icon storybook add example without lexicon-icon-* class and update tests ([c54d0e8](https://github.com/liferay/liferay-portal/commit/c54d0e88a327e88d2f3c85e185c0da292287db59))
- LPD-108570 clayui.com add aliases for product icon ([211b163](https://github.com/liferay/liferay-portal/commit/211b163c4ccb2d9a0d7bc2c7bc9aeee3c96a2dfd))

## @clayui/label

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/layout

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))

## @clayui/link

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/list

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))

## @clayui/loading-indicator

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/localized-input

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))

## @clayui/management-toolbar

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/modal

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-107024 @clayui/modal CardStyleModal update to use new modal-banner-img-* class ([8d431e5](https://github.com/liferay/liferay-portal/commit/8d431e5325a48ef079aedc12497c515b804622de))
- LPD-107488 Stack sibling modals above the modal below ([340e57b](https://github.com/liferay/liferay-portal/commit/340e57bcee446a389a734b0394b9cfbe1b03671c))
- LPD-107488 Keep modal-open on body until the last modal closes ([37e9b42](https://github.com/liferay/liferay-portal/commit/37e9b42962a90e7b1b6ba4081d92bcc802915dd9))
- LPD-107488 Add a Storybook story for stacked modals ([f17a006](https://github.com/liferay/liferay-portal/commit/f17a0067b32c3a332d4949cea55c31df5187e5c9))
- LPD-107488 Add tests for stacked modals ([a0efb90](https://github.com/liferay/liferay-portal/commit/a0efb90517d1b3b3cf41da52567e7ed85af54045))
- LPD-107488 Use jest-dom matchers in modal interaction tests ([ef8b0a4](https://github.com/liferay/liferay-portal/commit/ef8b0a42926df848ad26fa083947abc5c79bd394))

## @clayui/multi-select

### Commits

- LPD-100943 Align 'fuzzy' versions ([38131f3](https://github.com/liferay/liferay-portal/commit/38131f3273717a498d1e3c877bc70d9c986521d2))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/multi-step-nav

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-103692 Apply the className prop passed to MultiStepNav.Item ([cb29c3b](https://github.com/liferay/liferay-portal/commit/cb29c3b11996a8e5a7c03008990830bbd6238b6f))

## @clayui/nav

### Commits

- LPD-100943 Align 'react-transition-group' versions ([f154bb6](https://github.com/liferay/liferay-portal/commit/f154bb645cb315667f4369660b3598e47cd7d4a6))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))

## @clayui/navigation-bar

### Commits

- LPD-100943 Align 'react-transition-group' versions ([f154bb6](https://github.com/liferay/liferay-portal/commit/f154bb645cb315667f4369660b3598e47cd7d4a6))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))

## @clayui/pagination

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-105970 Let consumers localize the pagination aria labels ([5e4a126](https://github.com/liferay/liferay-portal/commit/5e4a1260b48a94a84231d492a0aa3d61a3ac3f0e))
- LPD-105970 Update tests, docs, and stories for the pagination aria labels ([11eba3b](https://github.com/liferay/liferay-portal/commit/11eba3bdb2e832f8434fce786cf23ceac4087bf0))
- LPD-105970 Update tests for the pagination aria labels ([345c41e](https://github.com/liferay/liferay-portal/commit/345c41e71390d4bcb58b0e657c4d0a57c31e69e5))

## @clayui/pagination-bar

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-105970 Let consumers localize the pagination aria labels ([5e4a126](https://github.com/liferay/liferay-portal/commit/5e4a1260b48a94a84231d492a0aa3d61a3ac3f0e))
- LPD-105970 Update tests, docs, and stories for the pagination aria labels ([11eba3b](https://github.com/liferay/liferay-portal/commit/11eba3bdb2e832f8434fce786cf23ceac4087bf0))
- LPD-105970 Update tests for the pagination aria labels ([345c41e](https://github.com/liferay/liferay-portal/commit/345c41e71390d4bcb58b0e657c4d0a57c31e69e5))

## @clayui/panel

### Commits

- LPD-100943 Align 'react-transition-group' versions ([f154bb6](https://github.com/liferay/liferay-portal/commit/f154bb645cb315667f4369660b3598e47cd7d4a6))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/popover

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/progress-bar

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))

## @clayui/provider

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-66970 Add persistentAlerts to the Clay Provider ([76f7354](https://github.com/liferay/liferay-portal/commit/76f73543d1c640af596f862bbeb54dede3509308))
- LPD-66970 Document persistentAlerts in the Provider and Alert docs ([8a152da](https://github.com/liferay/liferay-portal/commit/8a152daa27a019b96771100b41c92554fa3a6f79))

## @clayui/shared

### Commits

- LPD-100943 Align 'dom-align' versions ([f7b25c3](https://github.com/liferay/liferay-portal/commit/f7b25c311e46f41b489745ca04c7469c865244cf))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-103935 Measure the alignment source where the last pass placed it ([6417492](https://github.com/liferay/liferay-portal/commit/64174929503ea86a9d3bda5aaa344c45d8ab278e))
- LPD-106216 Keep the dropdown menu anchored to its trigger when it fits neither below nor above ([21de791](https://github.com/liferay/liferay-portal/commit/21de791529bd7af6fa605ca6a64805116ccb66c4))
- LPD-106216 Keep the menu scroll position across alignment passes ([6281385](https://github.com/liferay/liferay-portal/commit/62813857a4baf9d6e98524b2da4d299ce26384a0))
- LPD-106216 Restore the caller max height when the menu stops being constrained ([d450ddc](https://github.com/liferay/liferay-portal/commit/d450ddcc73933ae52d93bd721d6a1eb27237e570))
- LPD-106216 Move the menu height constraint to its own file and split it into named steps ([3f65826](https://github.com/liferay/liferay-portal/commit/3f658268759ad39b30dda142d15a2c7a130b67bd))
- LPD-106216 Pass named arguments to constrainHeightToViewport ([536b23c](https://github.com/liferay/liferay-portal/commit/536b23c48ab0035483584d0d9d17e8781f78b94b))
- LPD-106216 Keep the scroll position handling inside constrainHeightToViewport ([f5d18f6](https://github.com/liferay/liferay-portal/commit/f5d18f6ddaa7e886a5cc3f9ed6cdab29364ea2b6))
- LPD-106216 Add unit tests for constrainHeightToViewport ([838f07b](https://github.com/liferay/liferay-portal/commit/838f07be5cd458e986d6fef80d7fbcbf12b6b80b))

## @clayui/slider

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/sticker

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/table

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))

## @clayui/tabs

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Declare prettier in the clay packages that run it ([b049f48](https://github.com/liferay/liferay-portal/commit/b049f48735ca2a855fb43ef36c8ccf5668dd9e51))
- LPD-104123 Apply the className prop passed to Tabs.Item ([e6b0db3](https://github.com/liferay/liferay-portal/commit/e6b0db39963c6ec2029b351cc00776cd65272715))
- LPD-104123 Depend on @clayui/icon in the tabs package ([ec8f470](https://github.com/liferay/liferay-portal/commit/ec8f4708a7729c7c7d9316200b24e6fbc62c606e))
- LPD-104123 Add Tabs.ItemWithIcon ([8ea4e5d](https://github.com/liferay/liferay-portal/commit/8ea4e5d3d3ce2279bdfff399ba3bc4801a3bc338))
- LPD-104123 Document Tabs.ItemWithIcon ([901be9a](https://github.com/liferay/liferay-portal/commit/901be9afec3a5ec67c008ffe9e9733487c3abbc0))
- LPD-104123 Test Tabs.ItemWithIcon ([10027d7](https://github.com/liferay/liferay-portal/commit/10027d70951b8513fc6fe673dc592238ffb261c3))
- LPD-108195 Use the canonical Jest matcher names ([4192f7a](https://github.com/liferay/liferay-portal/commit/4192f7afeb1bc13784e974144dc4775fff04cc87))

## @clayui/time-picker

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LRCI-8267 Remove unnecessary endline character ([5d4a0ef](https://github.com/liferay/liferay-portal/commit/5d4a0ef437f97578d64167ec304fcdfbd95a862b))
- LPD-108222 Stop rendering unknown DOM properties ([d9451ba](https://github.com/liferay/liferay-portal/commit/d9451ba19cca5c06f80cca758b3d24e3a334032e))

## @clayui/toolbar

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Declare prettier in the clay packages that run it ([b049f48](https://github.com/liferay/liferay-portal/commit/b049f48735ca2a855fb43ef36c8ccf5668dd9e51))

## @clayui/tooltip

### Commits

- LPD-100943 Align 'dom-align' versions ([f7b25c3](https://github.com/liferay/liferay-portal/commit/f7b25c311e46f41b489745ca04c7469c865244cf))
- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Replace caret dependency ranges with explicit versions ([5d9331b](https://github.com/liferay/liferay-portal/commit/5d9331bb04a0d66b482ee4221590e1cb2b8b24d4))
- LPD-87545 Declare prettier in the clay packages that run it ([b049f48](https://github.com/liferay/liferay-portal/commit/b049f48735ca2a855fb43ef36c8ccf5668dd9e51))

## @clayui/upper-toolbar

### Commits

- LPD-100943 Align 'cross-env' versions ([982f5e3](https://github.com/liferay/liferay-portal/commit/982f5e318a93608aa202469a3e9ecf58a0f10d38))
- LPD-87545 Declare prettier in the clay packages that run it ([b049f48](https://github.com/liferay/liferay-portal/commit/b049f48735ca2a855fb43ef36c8ccf5668dd9e51))

# [3.166.0] (2026-08-10)

## @clayui/autocomplete

### Commits

- LPD-93196 Regenerate snapshots ([e6ccd5b](https://github.com/liferay/liferay-portal/commit/e6ccd5b2f95fb806399996099bc2955843735e17))

## @clayui/button

### Commits

- LPD-97474 Add an AI button variant to Clay's stylesheet ([bf6d9a1](https://github.com/liferay/liferay-portal/commit/bf6d9a15c2f5861a80f9ce400a77854c6937244b))
- LPD-97474 Add the ClayAiButton component and ai displayType ([f2ce392](https://github.com/liferay/liferay-portal/commit/f2ce3929b6e535e25aa918589f36ad0bb085f56d))
- LPD-97474 Cover the AI button children and label case in the story ([f18808e](https://github.com/liferay/liferay-portal/commit/f18808e71d8ffc4aaf2f8ed0c051839bf6dc55a0))
- LPD-97474 Rename ClayAiButton to ClayAIButton ([4f77058](https://github.com/liferay/liferay-portal/commit/4f7705886ff33fceb8bf07ea60b3576fb1fd94b3))

## @clayui/card

### Commits

- LPD-88758 Add size prop to the ClayCard component ([c393b73](https://github.com/liferay/liferay-portal/commit/c393b73e9e532a8e8f8f3de87974ad39506ce7f4))
- LPD-88758 LPD-88753 Add nested M hover with backdrop blur ([53c81e8](https://github.com/liferay/liferay-portal/commit/53c81e8e4ce5ce3755bc034a7e05498067b38ddc))
- LPD-88758 Rename card size prop values to xs/sm/md ([67afb69](https://github.com/liferay/liferay-portal/commit/67afb696f567c08d216786e5dfdfdd9a1c7e41b7))
- LPD-88758 Replace card size prop with card-page layout views ([537075f](https://github.com/liferay/liferay-portal/commit/537075f2f8774b4b21d1fd628053b22c671b6951))
- LPD-88758 Show card-page views with proper card types in story ([dc8b87d](https://github.com/liferay/liferay-portal/commit/dc8b87d1a475a56682f1007ad37cb59c3612356c))
- LPD-88758 Lay out card-page with CSS Grid supporting list markup ([5d3c6dc](https://github.com/liferay/liferay-portal/commit/5d3c6dce3acae930e77732a93c347c5d4a98a75d))
- LPD-88758 Storybook update card-page-dense and horizontal examples to require card-page class ([f25786c](https://github.com/liferay/liferay-portal/commit/f25786c73cb365cd94fe156cac8f3ad947bcd4e5))
- LPD-97442 Add aria-disable attribute if the card is disabled. This meets the accessibility requirements ([2ab5b44](https://github.com/liferay/liferay-portal/commit/2ab5b44588e6c6c26b12ea793b362e5351d90642))
- LPD-97442 Update snapshots ([b8c9b7f](https://github.com/liferay/liferay-portal/commit/b8c9b7fd6a4e418176e78347e3a710917d54effe))

## @clayui/color-picker

### Commits

- LPD-93196 Regenerate snapshots ([e6ccd5b](https://github.com/liferay/liferay-portal/commit/e6ccd5b2f95fb806399996099bc2955843735e17))

## @clayui/core

### Commits

- LPD-93194 Add aria-label to toggle switches ([1691fec](https://github.com/liferay/liferay-portal/commit/1691fec35dbdacf4edac5267ac345eaffadd1e5d))
- LPD-95233 @clayui/core side-panel allow passing props to close button ([f141cb6](https://github.com/liferay/liferay-portal/commit/f141cb6e3ec0fd8ea659c55530aa5c4ea5c252f5))
- LPD-95233 @clayui/core SidePanelHeader don't run onOpenChange if preventDefault ([2d29b1b](https://github.com/liferay/liferay-portal/commit/2d29b1b6d1c298d1463b301fc8b1b791da7dab87))
- LPD-93195 Fix invalid role in table column menu ([cc32fd2](https://github.com/liferay/liferay-portal/commit/cc32fd2e0e6a3f07510f303290529e62bacd17cb))
- LPD-98845 Honor a tabIndex prop on Clay's Picker ([1a2c434](https://github.com/liferay/liferay-portal/commit/1a2c43452b6fc260ace8712479339a3094c4c1ed))
- LPD-98845 Test the Picker tabIndex prop ([2d20515](https://github.com/liferay/liferay-portal/commit/2d20515f66b7ff350492d663c209a730e232d982))
- LPD-98879 Prevent page scroll when focusing the picker search ([2f95366](https://github.com/liferay/liferay-portal/commit/2f9536670c770d9bdc160d1f0ac623733b629b52))

## @clayui/css

### Commits

- LPD-93992 - Increase Secondary L0 contrast ([41ffb55](https://github.com/liferay/liferay-portal/commit/41ffb55b3689e08b4c853b5fb72b72e1c88a81b9))
- LPD-93241 Increase contrast of Clay date picker date cells ([8e58e10](https://github.com/liferay/liferay-portal/commit/8e58e106c1a5427eaf91e794f54354ff296b0d52))
- LPD-94630 Fix unitless 0 fallback for --multi-step-divider-spacer-x in atlas CSS ([a4676ee](https://github.com/liferay/liferay-portal/commit/a4676ee7a49ff62bc36cda34224174e2f77abae3))
- LPD-94804 Match side navigation header background to the menu body ([e63eb3f](https://github.com/liferay/liferay-portal/commit/e63eb3f7beea7cf61c38f68a2445bc8da0622c63))
- LPD-94305 Fix checkbox and radio alignment without an inline label ([bf36acb](https://github.com/liferay/liferay-portal/commit/bf36acbe4f24991ddedeffe7f81494d20a8ba285))
- LPD-94260 Fix translucent badge backgrounds for CSS-variable colors ([3289521](https://github.com/liferay/liferay-portal/commit/32895215261183e64d2b7a5861b49ce6ba6defe5))
- LPD-94260 SF ([c885bdf](https://github.com/liferay/liferay-portal/commit/c885bdfdff38f6bce6c3f64cd8b65d602cb7dd1f))
- LPD-92715 Fix Clay card hover flicker on the bottom border ([3f46c12](https://github.com/liferay/liferay-portal/commit/3f46c120c84b09b1568bf2b1b86001dcc9796587))
- LPD-95233 @clayui/css Add close sr-focusable-only utility ([94b2c8b](https://github.com/liferay/liferay-portal/commit/94b2c8bc87c21d14136a9c55d591e3c21032fe94))
- LPD-94747 Give each label size variant a unique placeholder ([c750aa6](https://github.com/liferay/liferay-portal/commit/c750aa607577f35a7896d817f20e9b460179a318))
- LPD-94747 Add a small size variant to ClayLabel ([dc24891](https://github.com/liferay/liferay-portal/commit/dc24891042686d78bc7bb1bef5b0133f5262f2d0))
- LPD-94747 SF ([e699f88](https://github.com/liferay/liferay-portal/commit/e699f885d9ba8e20e44e4584e04a9174b5f8935d))
- LPD-95743 Update Dark mode colors to dark-mode-reviewed-v2 ([ab4a07d](https://github.com/liferay/liferay-portal/commit/ab4a07d325ac696e3ca659f89d6f2f5331c337ad))
- LPD-95951 Change ClaySticker outline-1 variant to use indigo instead of purple ([0af2522](https://github.com/liferay/liferay-portal/commit/0af25227eff9bb1897183886160661425941d589))
- LPD-88758 Add card size and interaction state styling ([fe5f06e](https://github.com/liferay/liferay-portal/commit/fe5f06ed1217acee3762b9965ea198d516518096))
- LPD-88758 LPD-88753 Add nested M hover with backdrop blur ([53c81e8](https://github.com/liferay/liferay-portal/commit/53c81e8e4ce5ce3755bc034a7e05498067b38ddc))
- LPD-88758 Replace card size prop with card-page layout views ([537075f](https://github.com/liferay/liferay-portal/commit/537075f2f8774b4b21d1fd628053b22c671b6951))
- LPD-88758 Drop ticket reference from card-page comment ([63bb8bc](https://github.com/liferay/liferay-portal/commit/63bb8bce51069a2fc8743bdea55eff7b7fa6e730))
- LPD-88758 Extend card revamp to cadmin and atlas-custom-properties ([0cc4613](https://github.com/liferay/liferay-portal/commit/0cc4613d0e1cb99f68b6c3a5a59368267fde554c))
- LPD-88758 Lay out card-page with CSS Grid supporting list markup ([5d3c6dc](https://github.com/liferay/liferay-portal/commit/5d3c6dce3acae930e77732a93c347c5d4a98a75d))
- LPD-96101 Make cadmin form and component variables themeable ([b995bfe](https://github.com/liferay/liferay-portal/commit/b995bfe3547f5477c42e08805bed1e2d1d514133))
- LPD-95989 Define chart color and trend tokens in Clay ([07c6f6b](https://github.com/liferay/liferay-portal/commit/07c6f6b810a901f0b17833ad7579079759fe986b))
- LPD-95989 Darken the active blue bar within the blue ramp ([df91bbf](https://github.com/liferay/liferay-portal/commit/df91bbf5a482f889c4bf73d51740c9c4faae5966))
- LPD-98130 Fix typo in date picker nav btn active CSS variable name ([d2afddf](https://github.com/liferay/liferay-portal/commit/d2afddfe4448f52676ded1a44ac58484600e748e))
- LPD-97474 Add an AI button variant to Clay's stylesheet ([bf6d9a1](https://github.com/liferay/liferay-portal/commit/bf6d9a15c2f5861a80f9ce400a77854c6937244b))
- LPD-80363 Auto SF ([1607c7f](https://github.com/liferay/liferay-portal/commit/1607c7fd869bbd4677877d3f5d4dcce73a488ab6))
- LPD-80363 Sort ([f701926](https://github.com/liferay/liferay-portal/commit/f70192605e56021607aafc6ef2d85fd6be7b46df))
- LPD-80363 Sort ([3424ae8](https://github.com/liferay/liferay-portal/commit/3424ae8761264f6071356777068c5a394e0fbb65))
- LPD-80363 Sort ([576b0ca](https://github.com/liferay/liferay-portal/commit/576b0cae4e91a624e49918dae5a415ae3710c08e))
- LPD-80363 Sort ([fba7f5c](https://github.com/liferay/liferay-portal/commit/fba7f5cc12cb3d4ea8fd9e85450200d88e7e9aec))
- LPD-80363 Sort ([c9b9588](https://github.com/liferay/liferay-portal/commit/c9b9588452659b2918985d0e7d1aea4462d7349a))
- LPD-80363 Sort ([25e4dbb](https://github.com/liferay/liferay-portal/commit/25e4dbbb9ea03f695c8190a0fff3977bf173b1aa))
- LPD-80363 Auto SF ([5bf2bf3](https://github.com/liferay/liferay-portal/commit/5bf2bf3067e49cd817fdca7037f35433df0fbb89))
- LPD-80363 Sort and separate nested components with new line ([a9f31f2](https://github.com/liferay/liferay-portal/commit/a9f31f27cbda04859437c9127de217700eb154c8))
- LPD-80363 SF ([d953c94](https://github.com/liferay/liferay-portal/commit/d953c94e9dc3f05b46ee0f0179c2e430d89862d7))
- LPD-98590 atlas-custom-properties: --sidebar-light-sidebar-footer should be --sidebar-light-sidebar-footer-{background-color|border} ([df745cf](https://github.com/liferay/liferay-portal/commit/df745cf37f86deb9f57d297f133a6bedb5ce332d))
- LPD-95947 Reserve card hover animation for interactive cards ([c7df2b0](https://github.com/liferay/liferay-portal/commit/c7df2b0726bfc60fbc7f036fd037ab244d932804))
- LPD-98586 atlas-custom-properties: --nav-tabs-font-size is already declared in $nav-tabs-font-size ([082ac48](https://github.com/liferay/liferay-portal/commit/082ac480eb5c960bff23921323e186941f7383fd))
- LPD-97009 @clayui/css add input-group-ai ([f261aaf](https://github.com/liferay/liferay-portal/commit/f261aaf3e96a56d631eee2e3da309a66b6f36f42))
- LPD-97009 @clayui/form adds InputGroupAi ([ac630a9](https://github.com/liferay/liferay-portal/commit/ac630a9e328fb0113250cb75a0786c2709194025))
- LPD-97009 @clayui/css input-grooup-ai icons and buttons should be aligned at the top ([37e8c73](https://github.com/liferay/liferay-portal/commit/37e8c73e0604a22d3e8e26648243f42bb021e474))
- LPD-99666 @clayui/css breakpoint-down shouldn't print ([c0697cd](https://github.com/liferay/liferay-portal/commit/c0697cd759f138793699fbbedc966dad3c7f2ddb))
- LPD-99666 @clayui/css convert variables using old breakpoint-down keys to use media-breakpoint-down ([3c4b248](https://github.com/liferay/liferay-portal/commit/3c4b2484b72bcfc30361bbd450137fd1810ceecb))
- LPD-99666 @clayui/css fix invalid CSS property border-top-radius ([20657a7](https://github.com/liferay/liferay-portal/commit/20657a7b4665a5fe740fcbc9da9f29eed753a9c6))
- LPD-99666 @clayui/css Table caption prints invalid CSS padding-bottom: 0.5rem 1rem; ([0eac711](https://github.com/liferay/liferay-portal/commit/0eac7113c0a5988f64de6866bc48ad909f0732fe))
- LPD-96102 Add an AI variant to Clay's dropdown menu ([21d2e6d](https://github.com/liferay/liferay-portal/commit/21d2e6dc62fb6f67dfe9b80075df6596388f6baa))
- LPD-96102 Add arrow-key navigation and address AI dropdown review ([adb07b9](https://github.com/liferay/liferay-portal/commit/adb07b96c655999a4b723cb49b58112106b76aa8))
- LPD-99928 LPD-101285 buildIcons ([0bb940b](https://github.com/liferay/liferay-portal/commit/0bb940b9782c115043bf0d662b74572f83258d51))
- LPD-99928 @clayui/css add agent.svg and chat-spark.svg ([3326a35](https://github.com/liferay/liferay-portal/commit/3326a35410bc26600906f053654ba5626ea6240a))
- LPD-101285 @clayui/css add mcp.svg ([7e5bbca](https://github.com/liferay/liferay-portal/commit/7e5bbca2befe882b59e429fdc0d379b8cc5183a9))
- LPD-100366 Reserve the card pressed state for interactive cards ([011b398](https://github.com/liferay/liferay-portal/commit/011b398e5a0012297d21bcce5bb9d8efb017b71d))

## @clayui/date-picker

### Commits

- LPD-93196 Regenerate snapshots ([e6ccd5b](https://github.com/liferay/liferay-portal/commit/e6ccd5b2f95fb806399996099bc2955843735e17))

## @clayui/drop-down

### Commits

- LPD-93196 Give dropdown menu tabindex 0 when active ([b248ebe](https://github.com/liferay/liferay-portal/commit/b248ebea6774ce74565c2d49e8a9f6b53a4836cd))
- LPD-93196 Update clay-drop-down snapshots ([3be760e](https://github.com/liferay/liferay-portal/commit/3be760ec8f300092831b2794bbd1fc980a7e2dd4))
- LPD-93196 Regenerate snapshots ([e6ccd5b](https://github.com/liferay/liferay-portal/commit/e6ccd5b2f95fb806399996099bc2955843735e17))
- LPD-96102 Add an AI variant to Clay's dropdown menu ([21d2e6d](https://github.com/liferay/liferay-portal/commit/21d2e6dc62fb6f67dfe9b80075df6596388f6baa))
- LPD-96102 Add the ClayDropDownWithAI flow component ([451ec5c](https://github.com/liferay/liferay-portal/commit/451ec5c78b6ae26f34759517b3ca0bdec405aa02))
- LPD-96102 Improve ClayDropDownWithAI accessibility ([812c5ec](https://github.com/liferay/liferay-portal/commit/812c5ec9ec85ea7308061f2f6f379571da5043c8))
- LPD-96102 Render ClayDropDownWithAI states from a state map ([e39da7b](https://github.com/liferay/liferay-portal/commit/e39da7b75ce9446d51bbf81083807c346ff55b99))
- LPD-96102 Expand the ClayDropDownWithAI story example ([b8d9849](https://github.com/liferay/liferay-portal/commit/b8d98494c874a7130ce376d30c546c22beb13b76))
- LPD-96102 Streamline ClayDropDownWithAI internals ([e610cb5](https://github.com/liferay/liferay-portal/commit/e610cb5bb45ca8a79e3cf4215471e10e88e435cd))
- LPD-96102 Add an onStop callback to ClayDropDownWithAI ([9d26433](https://github.com/liferay/liferay-portal/commit/9d26433aded2820121c67aa6c1ec96ae021ce358))
- LPD-96102 Add arrow-key navigation and address AI dropdown review ([adb07b9](https://github.com/liferay/liferay-portal/commit/adb07b96c655999a4b723cb49b58112106b76aa8))
- LPD-100332 Use ClayInputGroupAI for the ClayDropDownWithAI prompt input ([0f7c919](https://github.com/liferay/liferay-portal/commit/0f7c9194ea577a72af03a6f961eadc0ad9e903bb))

## @clayui/form

### Commits

- LPD-97009 @clayui/form adds InputGroupAi ([ac630a9](https://github.com/liferay/liferay-portal/commit/ac630a9e328fb0113250cb75a0786c2709194025))
- LPD-97009 @clayui/form Storybook add InputGroupAi example ([25166ec](https://github.com/liferay/liferay-portal/commit/25166ecb5dd8b5454e13b625b46dc2e5b06e7866))
- LPD-97009 @clayui/form InputGroupAI tests ([9de0a2b](https://github.com/liferay/liferay-portal/commit/9de0a2bc88ebc82243010f78e4ea36f1377e88c6))
- LPD-97009 @clayui/form InputGroupAI should resize when submitting or deleting text ([8184678](https://github.com/liferay/liferay-portal/commit/81846782c30cdf1c8ee1dd247e260c5806e47b80))
- LPD-97009 @clayui/form Update InputGroupAI test ([c3ee9dc](https://github.com/liferay/liferay-portal/commit/c3ee9dc9333ea31a422fd501fcb55ad593902a2d))
- LPD-100332 Let ClayInputGroupAI override its default buttons with children ([f43bad3](https://github.com/liferay/liferay-portal/commit/f43bad3ef48073c8ac39fb655cf52ead8c1ab1ab))

## @clayui/icon

### Commits

- LPD-99928 clayui.com add aliases for agent and chat-spark icons ([89d5ccd](https://github.com/liferay/liferay-portal/commit/89d5ccd91f5fea447e94eca020b44360667314db))
- LPD-101285 clayui.com add aliases for mcp icon ([02de93c](https://github.com/liferay/liferay-portal/commit/02de93ce7c120bde8b6536e7ebbd1e70218819c3))

## @clayui/label

### Commits

- LPD-94747 Add a small size variant to ClayLabel ([dc24891](https://github.com/liferay/liferay-portal/commit/dc24891042686d78bc7bb1bef5b0133f5262f2d0))
- LPD-90973 Expose Clay Label named exports to the DXP export bridge ([e671da4](https://github.com/liferay/liferay-portal/commit/e671da4e51d2b138b8db0781ce816d49f71779fe))

# [3.165.0] (2026-06-05)

## @clayui/badge

### Commits

- LPD-83292 Document the ClayBadge icon variant and add a Storybook story ([d6c279b](https://github.com/liferay/liferay-portal/commit/d6c279b70540fca1b26ccd1f46365689dfd59ee3))
- LPD-83292 Add a SeeAll Badge story covering all icon display types ([c6c6ac3](https://github.com/liferay/liferay-portal/commit/c6c6ac3a56e927ddc221fbd280d319f2beb4e5f8))

## @clayui/css

### Commits

- LPD-93246 SF ([55c262d](https://github.com/liferay/liferay-portal/commit/55c262d7cbe367c92a752227df9c1e7335b73323))
- LPD-93019 Use the -md border radius for inverse and bold labels ([7b7d581](https://github.com/liferay/liferay-portal/commit/7b7d581445e61dd5af25e365351df085cd8a29f3))

# [3.164.0] (2026-06-02)

## @clayui/autocomplete

### Commits

- LPD-88727 Wire KeyboardArrowsIndicator into Autocomplete ([92158ce](https://github.com/liferay/liferay-portal/commit/92158ceb82013386c9fae3d75d30ada66a28edbf))
- LPD-39987 Make KeyboardArrowsIndicator purely visual (aria-hidden) ([32442e9](https://github.com/liferay/liferay-portal/commit/32442e98739cfe6ccbba503159df55d8a61440c9))
- LPD-39987 Await userEvent in keyboard arrows indicator tests ([4759391](https://github.com/liferay/liferay-portal/commit/47593914f62fa2e0b68ba80cf26ac251a7b067a4))
- LPD-74696 Improve Enter key handling in Clay autocomplete ([1db5b27](https://github.com/liferay/liferay-portal/commit/1db5b274c0ecec12156ce7ed21af53e2619be8bc))

## @clayui/card

### Commits

- LPD-79840 Add disabled class to Clay Card component ([49cc884](https://github.com/liferay/liferay-portal/commit/49cc884d66d34b1af78fda8e6877946f11946732))
- LPD-91533 Add taller card example to ensure checkbox is centered ([082d73f](https://github.com/liferay/liferay-portal/commit/082d73f9d89a32f7af05ed1c6459210b585500f0))
- LPD-91533 Extract shared actions array in card stories ([c24dcca](https://github.com/liferay/liferay-portal/commit/c24dcca2ddf534bdd9903867675bbed232137520))

## @clayui/core

### Commits

- LPD-89790 @clayui/core use composite icons in KeyboardArrowsIndicator ([4dda27f](https://github.com/liferay/liferay-portal/commit/4dda27f21ba2877e1b6f8ca5f06ddb4f149712d5))
- LPD-89790 Move arrows-all colors from SVG to CSS for dark-mode parity ([a47512c](https://github.com/liferay/liferay-portal/commit/a47512cca39cc7ec6f1aa5682ae48b680bf8e76b))
- LPD-90345 Require aria-controls on ResizeHandle ([b1e0cc6](https://github.com/liferay/liferay-portal/commit/b1e0cc68fe834342f3ca9e69bbaa811f2becf836))
- LPD-90345 Set aria-label by default ([758cce6](https://github.com/liferay/liferay-portal/commit/758cce6ca7833cd7c537382822b7cb311a8fa644))
- LPD-90345 Set aria-valuetext by default ([f7fb48b](https://github.com/liferay/liferay-portal/commit/f7fb48be1d1788443be25e55161b7fffd3746f4a))
- LPD-90345 Adapt the documentation ([2b71242](https://github.com/liferay/liferay-portal/commit/2b71242cc5b10985ea92e21e3fbafa762cea7fc4))
- LPD-90345 Prevent className override on ResizeHandle ([a294a28](https://github.com/liferay/liferay-portal/commit/a294a281c6fd99d670a4e56d1651e3a3932b861b))
- LPD-90345 Resolve SidePanel id in render for ResizeHandle aria-controls ([a7e7dcb](https://github.com/liferay/liferay-portal/commit/a7e7dcb322c99859568fb47ea902c4e38497d075))
- LPD-90345 Adapt snapshots ([f9f979b](https://github.com/liferay/liferay-portal/commit/f9f979b2b80ced0f3e7afd2f921f1c720b23f3d1))
- LPD-39987 Add tooltip-style anchored placement to KeyboardArrowsIndicator ([847d600](https://github.com/liferay/liferay-portal/commit/847d600a9dcd49d999a0fcbd575a101ec4e5cfa5))
- LPD-88716 Wire KeyboardArrowsIndicator into Picker ([74ae0fa](https://github.com/liferay/liferay-portal/commit/74ae0facfbd51441c5642e73921d1f0fbf30edad))
- LPD-88725 Wire KeyboardArrowsIndicator into IconSelector ([af0ef9c](https://github.com/liferay/liferay-portal/commit/af0ef9c7ef687343905500acc371079042684409))
- LPD-88730 Wire KeyboardArrowsIndicator into VerticalBar ([134f2d2](https://github.com/liferay/liferay-portal/commit/134f2d2472848c983a41ef80dcba663c4e8f06a1))
- LPD-88732 Wire KeyboardArrowsIndicator into VerticalNav ([2ca6383](https://github.com/liferay/liferay-portal/commit/2ca638379dfea77325dffff596ff3b82c2df80d7))
- LPD-91699 Wire KeyboardArrowsIndicator into ResizeHandle ([2f6f6a0](https://github.com/liferay/liferay-portal/commit/2f6f6a02c0a977910fc11107454a69788f7842bc))
- LPD-39987 Make KeyboardArrowsIndicator purely visual (aria-hidden) ([32442e9](https://github.com/liferay/liferay-portal/commit/32442e98739cfe6ccbba503159df55d8a61440c9))
- LPD-39987 Scope the indicator's focus listener to the anchor element ([c932951](https://github.com/liferay/liferay-portal/commit/c93295144e73643c45b3979db01fb9829fa0e06d))
- LPD-88739 Wire KeyboardArrowsIndicator into Table ([f8165ec](https://github.com/liferay/liferay-portal/commit/f8165ece71a331f89f4a794cb25581382345a316))
- LPD-39987 Add Center alignment and tooltip-top indicator placement ([d89b5a9](https://github.com/liferay/liferay-portal/commit/d89b5a9b126bcd6da58e2fc0c6ac91eefa26534a))
- LPD-39987 Await userEvent in keyboard arrows indicator tests ([4759391](https://github.com/liferay/liferay-portal/commit/47593914f62fa2e0b68ba80cf26ac251a7b067a4))
- LPD-39987 Fix Picker indicator assertion when no img role is in the DOM ([52c62b9](https://github.com/liferay/liferay-portal/commit/52c62b9c2b7952b5c511266ffcc84696d38f7ad2))
- LPD-39987 Mock fetch per-test in IconSelector indicator suite ([bdeccb7](https://github.com/liferay/liferay-portal/commit/bdeccb704d6155666f13d4c1c5ddad80dee3dba3))
- LPD-39987 Query portaled indicator from document.body in VerticalNav tests ([732245c](https://github.com/liferay/liferay-portal/commit/732245c359646a0ef729f57693c477c14a0f15d6))
- LPD-31376 TreeViewGroup switched from implicit findDOMNode lookup to the explicit nodeRef opt-in ([0c44546](https://github.com/liferay/liferay-portal/commit/0c445468eacf46799621134ae04d5523d8bae9b5))
- LPD-31376 Refactor functions declarations ([944d8ef](https://github.com/liferay/liferay-portal/commit/944d8efa224dcee27fa34128009a033403774850))

## @clayui/css

### Commits

- LPD-89790 @clayui/css replace arrow-key-* with arrows-* icons ([a273124](https://github.com/liferay/liferay-portal/commit/a27312488a14c74f3f274d72fac4047d4b69b721))
- LPD-89790 SF ([5185c59](https://github.com/liferay/liferay-portal/commit/5185c590694d52031b7f0b6a24feb7f973da8eb6))
- LPD-89790 Move arrows-all colors from SVG to CSS for dark-mode parity ([a47512c](https://github.com/liferay/liferay-portal/commit/a47512cca39cc7ec6f1aa5682ae48b680bf8e76b))
- LPD-89790 SF ([5f18e93](https://github.com/liferay/liferay-portal/commit/5f18e93f9704669f24602eb74c8780e6f73ef6bc))
- LPD-89790 Move lexicon-icon size utilities below .clay-keyboard-arrows-indicator ([0ef588f](https://github.com/liferay/liferay-portal/commit/0ef588fbd82e18f7548280d47363ed72e2a68b28))
- LPD-89790 Wrap hsl(from ...) in unquote for liferay-theme-tasks Sass ([5c657ab](https://github.com/liferay/liferay-portal/commit/5c657abc52cbcd18cd5dcbfaa5eae6296a043362))
- LPD-79840 Apply quick-win visual improvements to the existing Clay Card component ([7730ec2](https://github.com/liferay/liferay-portal/commit/7730ec20b16f9ba33c5f96d9f70f2d7821138597))
- LPD-79840 Add disabled class to Clay Card component ([49cc884](https://github.com/liferay/liferay-portal/commit/49cc884d66d34b1af78fda8e6877946f11946732))
- LPD-89211 Exclude .ck from being affected by .cadmin CSS ([a4c64a0](https://github.com/liferay/liferay-portal/commit/a4c64a04fe11c6e64a5070dc648e6a87e0b81732))
- LPD-88103 Update Clay label border-radius and text-transform ([1fa5890](https://github.com/liferay/liferay-portal/commit/1fa5890051a9c239bef0b3d80a0a39cb1d89bd4f))
- LPD-88103 Restyle Clay label colors and font sizes ([4ec5e0c](https://github.com/liferay/liferay-portal/commit/4ec5e0ce7cc673496a90a687ae786565bb3d1d36))
- LPD-88103 Apply color-d2 level to the text of Clay inverse labels ([de07090](https://github.com/liferay/liferay-portal/commit/de07090b1a42ac826d0ccfa48187c931e8fb415c))
- LPD-88103 Prevent unexpected changes for existing themes that extend styled by default ([328fe19](https://github.com/liferay/liferay-portal/commit/328fe19f8949aad0535898da0f9cf01472182b36))
- LPD-88103 Fix Atlas colors after removing from base ([7ae219a](https://github.com/liferay/liferay-portal/commit/7ae219a115b6bab7897ffcd75d11c6363ec8afac))
- LPD-90345 Add active and focus-visible styles to the resizer ([b08a245](https://github.com/liferay/liferay-portal/commit/b08a2450003323c3e093c7d9b7950eb84b889998))
- LPD-90345 Preserve horizontal resizer placement under c-prefers-focus-ring ([36ac1eb](https://github.com/liferay/liferay-portal/commit/36ac1eb27cf6fa8ef4e8b18b31b5948f308a161c))
- LPD-91067 Selected state of Cards not applying the primary stroke correctly ([8e8ccc7](https://github.com/liferay/liferay-portal/commit/8e8ccc7e27c077410144ed4e2b8c680110b62cc7))
- LPD-91067 Remove form-check-card when it's disabled ([0171777](https://github.com/liferay/liferay-portal/commit/017177766d886f1b0b22684bc03e22b9c86d7dc9))
- LPD-90973 Add Content Label variants to Clay ([69271a3](https://github.com/liferay/liferay-portal/commit/69271a3795e2ae19949251d0da3d6eb2324c0019))
- LPD-90973 Make border transparent for inverse ClayLabel component ([6a7f2cd](https://github.com/liferay/liferay-portal/commit/6a7f2cd65122211a81e855d8952fc7e0847b7e5e))
- LPD-39987 Add tooltip-style anchored placement to KeyboardArrowsIndicator ([847d600](https://github.com/liferay/liferay-portal/commit/847d600a9dcd49d999a0fcbd575a101ec4e5cfa5))
- LPD-92126 Decouple tabindex and type label styling from href ([33387a9](https://github.com/liferay/liferay-portal/commit/33387a9056ac7959b395ea6101eea40e9922ccaf))
- LPD-87424 Set $mark-bg to $yellow-l3 across atlas and cadmin ([fd36852](https://github.com/liferay/liferay-portal/commit/fd36852a3d33be059ccef03d13f0098e9670323c))
- LPD-92129 This rule should apply only to Safari and be avoided in any other browser ([67b2e5b](https://github.com/liferay/liferay-portal/commit/67b2e5b6884f2dc862fcab39e7c82ca10d0c4ba8))
- LPD-91533 Checkbox and Radio buttons should be vertically centered in horizontal cards ([9d2fe7f](https://github.com/liferay/liferay-portal/commit/9d2fe7f6d523b1859f3ddab398d32befc1399b24))
- LPD-92478 Use secondary-l2 for Clay Table borders ([c56359f](https://github.com/liferay/liferay-portal/commit/c56359f7ac49d8f76c60031da2bd167ab6f21068))
- LPD-92478 Use gray-400 for Clay Table borders ([482b12d](https://github.com/liferay/liferay-portal/commit/482b12d15a57598f04e39285293d0d2ce9a05c9c))
- LPD-90766 @clayui/css add Analytics Cloud and chart icons ([15745da](https://github.com/liferay/liferay-portal/commit/15745dad9b080cd95c5e604fdc5427d9f40b91c1))
- LPD-90766 SF ([e5fcc00](https://github.com/liferay/liferay-portal/commit/e5fcc00c05147e56e5cb82f81ab8e083b057f149))
- LPD-85958 Convert Cadmin to use CSS custom properties and add color schemes for dark/light mode ([22d2180](https://github.com/liferay/liferay-portal/commit/22d218049cd912fb7b6b5f87fc9cbdbce167a93c))
- LPD-85958 Atlas Custom Properties adds light, dark, dark-high-contrast color schemes     - Use `unquote('hsl()')` pattern to prevent Sass from using Sass color functions instead of CSS color functions     - Source Formatting ([52b1f2d](https://github.com/liferay/liferay-portal/commit/52b1f2d747499294b017905244a8c55a330b0c69))
- LPD-85958 Clay Atlas / Base convert hardcoded colors to use global color variables ([3b1a916](https://github.com/liferay/liferay-portal/commit/3b1a91689764c2932286337cbc75b34c43751b89))
- LPD-85958 Update Clay dark and dark high contrast to newer values ([b2f2bbb](https://github.com/liferay/liferay-portal/commit/b2f2bbb4e4e066ac892fe0f1966053e327566673))

## @clayui/date-picker

### Commits

- LPD-91601 Support min and max in ClayDatePicker ([beaeb2e](https://github.com/liferay/liferay-portal/commit/beaeb2e5120791b39b77fd8dd8b08df8e44264b5))
- LPD-91601 Use aria-disabled for out-of-range days ([4350906](https://github.com/liferay/liferay-portal/commit/435090652097b708620dac46b1e5ae0e28a88ab9))
- LPD-91601 Announce out-of-range rejections to assistive technology ([8fe1760](https://github.com/liferay/liferay-portal/commit/8fe1760bec5e047a697881052f22e7df2d0d31d8))
- LPD-91601 Clone returned date in clamp to avoid mutation aliasing ([056ce31](https://github.com/liferay/liferay-portal/commit/056ce317bd4d93c23c0361dc8c6fbc21dcd1c38e))
- LPD-91601 Route disabled-day clicks through the parent guard ([8e04396](https://github.com/liferay/liferay-portal/commit/8e043967ee6b4b90aeb8df5c985ad4f1548aa912))
- LPD-91601 Tighten min/max tests with userEvent and jest-dom matchers ([e33ad92](https://github.com/liferay/liferay-portal/commit/e33ad928d81b5a65fdb2804b9d0b0d650bfc91f8))
- LPD-91601 Replace classList.toContain with toHaveClass in date-picker tests ([aab1bb6](https://github.com/liferay/liferay-portal/commit/aab1bb69312aa22ba0a9c98a80a1ce6d30a3e9ec))
- LPD-91601 Narrow filter predicate to keep forEach callback typed as string ([b3d7a9d](https://github.com/liferay/liferay-portal/commit/b3d7a9d43f35d7af9dd6592fa12575d83a54cf84))
- LPD-91601 Type the input variable as HTMLInputElement in min/max tests ([42f73a0](https://github.com/liferay/liferay-portal/commit/42f73a075413a039ff58e1480168c998684a15a0))
- LPD-88738 Wire KeyboardArrowsIndicator into DatePicker ([8926099](https://github.com/liferay/liferay-portal/commit/8926099961144f406799bd55e0dc5db694eba369))
- LPD-39987 Make KeyboardArrowsIndicator purely visual (aria-hidden) ([32442e9](https://github.com/liferay/liferay-portal/commit/32442e98739cfe6ccbba503159df55d8a61440c9))

## @clayui/drop-down

### Commits

- LPD-44313 Forward other props on ClayDropDownWithItems ([f961f21](https://github.com/liferay/liferay-portal/commit/f961f2150ee61a0e3ee1943cccac2ccef1e584b2))
- LPD-44313 Forward other props on ClayDropDownWithDrilldown ([cd67bd9](https://github.com/liferay/liferay-portal/commit/cd67bd97cc3152ab2e8cdd737d1e3d9431dea1b3))
- LPD-44313 Extract shared DropDownHTMLAttributes type ([992cc26](https://github.com/liferay/liferay-portal/commit/992cc2625eaf8f053d8fdbe7c802f9a94701e945))
- LPD-44313 Stop spreading messages prop into nested DropDown JSX ([1d447cd](https://github.com/liferay/liferay-portal/commit/1d447cd0a529975a55fc29aea8d6b75fd91c3bd9))
- LPD-88711 Wire KeyboardArrowsIndicator into DropDown ([2e63df8](https://github.com/liferay/liferay-portal/commit/2e63df85ecb58fe2b87933419c5f4b48bccdf5de))
- LPD-39987 Make KeyboardArrowsIndicator purely visual (aria-hidden) ([32442e9](https://github.com/liferay/liferay-portal/commit/32442e98739cfe6ccbba503159df55d8a61440c9))
- LPD-34943 Keep parent overlay accessible when nested overlay opens ([2f70757](https://github.com/liferay/liferay-portal/commit/2f7075735c83bd653bb1ea7650c5c9143cfef2ff))
- LPD-34943 Tighten overlay accessibility test and Overlay filter ([40bc7a8](https://github.com/liferay/liferay-portal/commit/40bc7a8c40d5aeae2dba0c1da89f1c36e24486b2))

## @clayui/icon

### Commits

- LPD-89790 clayui.com replace arrow-key-* with arrows-* and aliases ([b281edd](https://github.com/liferay/liferay-portal/commit/b281edd325a06a39e80cb5cf3c958036acc5ec81))
- LPD-90766 clayui.com add aliases for Analytics Cloud and chart icons ([b350410](https://github.com/liferay/liferay-portal/commit/b350410d75ecef5286745f94414d9bc8f21e9d31))

## @clayui/label

### Commits

- LPD-88103 Add inverse variant to the Clay Label component ([dc876ae](https://github.com/liferay/liferay-portal/commit/dc876ae88674304b023d8c78dab56f1e096fb7c0))
- LPD-88103 Restyle Clay label colors and font sizes ([4ec5e0c](https://github.com/liferay/liferay-portal/commit/4ec5e0ce7cc673496a90a687ae786565bb3d1d36))
- LPD-88103 Prevent inverse for unstyled display type ([979adc7](https://github.com/liferay/liferay-portal/commit/979adc7c1d2a2d02b7ae817b41c7493e5b2b6e05))
- LPD-88103 Refactor css classes assignment ([a4e1598](https://github.com/liferay/liferay-portal/commit/a4e159853165e84a515409914d5cb83b2568fcb8))
- LPD-90973 Add Content Label variants to Clay ([69271a3](https://github.com/liferay/liferay-portal/commit/69271a3795e2ae19949251d0da3d6eb2324c0019))
- LPD-90973 Add ContentLabel React component ([17ddab9](https://github.com/liferay/liferay-portal/commit/17ddab94c90c6d08ee363d80a3f7ab299b0233af))
- LPD-90973 Make displayType only optional for ClayLabel component ([460e4ff](https://github.com/liferay/liferay-portal/commit/460e4ff6a66079adc1dc19624fcb3ebd83b570b1))
- LPD-90973 Split Default and SeeAll stories for ClayLabel component ([ee04483](https://github.com/liferay/liferay-portal/commit/ee044832d7448770dd980ca907334c6bfd1e47e6))

## @clayui/multi-select

### Commits

- LPD-88735 Wire KeyboardArrowsIndicator into MultiSelect ([cd1ac5c](https://github.com/liferay/liferay-portal/commit/cd1ac5cf55019889705d9e01f584597cb7123c10))
- LPD-39987 Make KeyboardArrowsIndicator purely visual (aria-hidden) ([32442e9](https://github.com/liferay/liferay-portal/commit/32442e98739cfe6ccbba503159df55d8a61440c9))
- LPD-39987 SF ([8d4739e](https://github.com/liferay/liferay-portal/commit/8d4739e95a07b6065ea3970837b40c52bc3b0f1e))
- LPD-39987 Move MultiSelect indicator portal outside Container ([7f7918a](https://github.com/liferay/liferay-portal/commit/7f7918ab30ce3b2e64205257497e7cb0a912fea9))
- LPD-39987 Await userEvent in keyboard arrows indicator tests ([4759391](https://github.com/liferay/liferay-portal/commit/47593914f62fa2e0b68ba80cf26ac251a7b067a4))
- LPD-74696 Improve Enter key handling in Clay autocomplete ([1db5b27](https://github.com/liferay/liferay-portal/commit/1db5b274c0ecec12156ce7ed21af53e2619be8bc))
- LPD-74696 Add an allowsCustomLabel story to the MultiSelect storybook ([858ae94](https://github.com/liferay/liferay-portal/commit/858ae944333e08abceb041524e9e86d52bb24f61))

## @clayui/shared

### Commits

- LPD-39987 Add Center alignment and tooltip-top indicator placement ([d89b5a9](https://github.com/liferay/liferay-portal/commit/d89b5a9b126bcd6da58e2fc0c6ac91eefa26534a))
- LPD-34943 Keep parent overlay accessible when nested overlay opens ([2f70757](https://github.com/liferay/liferay-portal/commit/2f7075735c83bd653bb1ea7650c5c9143cfef2ff))
- LPD-34943 Tighten overlay accessibility test and Overlay filter ([40bc7a8](https://github.com/liferay/liferay-portal/commit/40bc7a8c40d5aeae2dba0c1da89f1c36e24486b2))

# [3.163.0] (2026-05-13)

## @clayui/alert

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-81627 Allow passing classNames to the container ([1f9f4da](https://github.com/liferay/liferay-portal/commit/1f9f4da502edcfdd557a19645e383624cf9bb1fc))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/autocomplete

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82600 Fix Escape key input ([e02bcac](https://github.com/liferay/liferay-portal/commit/e02bcac12cbca7dd8d379f2b6a1ab81928aab56b))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/badge

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83029 Allow passing icon to badge ([26ceeb3](https://github.com/liferay/liferay-portal/commit/26ceeb3731afc917d0621aea45d967f16d2df0e5))
- LPD-83029 Add test ([ffa79b5](https://github.com/liferay/liferay-portal/commit/ffa79b5ed6336425bf6a5e9e06574a78ece42b58))
- LPD-83029 Implement ItemAfter, ItemBefore and ItemExpand logic ([19fcf1f](https://github.com/liferay/liferay-portal/commit/19fcf1f41c0cbb34cefb00bcd51f9cfcce7722b1))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/breadcrumb

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/button

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/card

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-83803 Make ClayCardWithInfo img not draggable ([e6b9674](https://github.com/liferay/liferay-portal/commit/e6b9674a2d9f3ed65f2173fae2c7b0f00ed27940))
- LPD-83803 Update test snapshots ([208ed9b](https://github.com/liferay/liferay-portal/commit/208ed9bde0b51d3e2f1e0b84331394944c607130))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/color-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82601 Remove skip, test is working fine ([e0ad6fe](https://github.com/liferay/liferay-portal/commit/e0ad6fee201ce87c88db7b00453a265ee9c35785))
- LPD-80236 Convert index.ts into barrel file for exports ([ca8603a](https://github.com/liferay/liferay-portal/commit/ca8603a4049edfe93d16ad4b4dfa618362e35030))
- LPD-80236 Add useColorPicker hook ([82a441e](https://github.com/liferay/liferay-portal/commit/82a441e1e9a3969ae9e09d19e46607e304707916))
- LPD-80236 Add Field component to isolate the color input, splotch and dropdown ([74de4f2](https://github.com/liferay/liferay-portal/commit/74de4f237c92208742c97a8d25aae7157cb40375))
- LPD-80236 Export useColorPicker hook, Field and Editor components to reuse them ([2b55a42](https://github.com/liferay/liferay-portal/commit/2b55a42394e109edf4e454a4ea8e1cffd01cbe39))
- LPD-80236 Add the ability to add an external trigger reference ([13892d2](https://github.com/liferay/liferay-portal/commit/13892d25edd143ec7204c5c96b6870058ea9993a))
- LPD-80236 Adapt the parseColorValue function to the ClayColorPicker, taking into account the hex6 and hex8 ([c9d85e9](https://github.com/liferay/liferay-portal/commit/c9d85e96be51906c6c8699d95936255486f17418))
- LPD-80236 Save the value when onBlur is done in the Editor hex input ([ab85832](https://github.com/liferay/liferay-portal/commit/ab85832fee860e17e1a6df45b397987055a23059))
- LPD-80236 Improve props and descriptions ([6da7abb](https://github.com/liferay/liferay-portal/commit/6da7abb3a3dc9f6afcbf4eb24febbe592d4af6d9))
- LPD-80236 Rename onClickSplotch to onSplotchClick ([bec76ac](https://github.com/liferay/liferay-portal/commit/bec76acc04c3bbd9ed357adf790094fde7f09e29))
- LPD-80236 Create new function to format the hex colors ([bed726a](https://github.com/liferay/liferay-portal/commit/bed726a2fb3ce44c76a4157ac4adb2c93a77de40))
- LPD-83988 Export all the necessary functions from util.ts ([fdd7b2c](https://github.com/liferay/liferay-portal/commit/fdd7b2c82fcd839f34d5f5f7f29bd27b827c74c4))
- LPD-83988 Update the package types in the color-picker docs ([d2b0084](https://github.com/liferay/liferay-portal/commit/d2b00844c744b7175051336914471d4dd3d19957))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/core

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78752 Allow configuring custom classes in Clay SidePanel.Title ([5626239](https://github.com/liferay/liferay-portal/commit/56262393602378d933b9d7962fc609700b4e8407))
- LPD-79027 Use existing onEnd callback ([d6d039d](https://github.com/liferay/liferay-portal/commit/d6d039d7073cbc6558ca4ab14bc08eb372c035bb))
- LPD-79027 Reset state also after drop with mouse ([03de461](https://github.com/liferay/liferay-portal/commit/03de46132d6ac765df42adc6452da3ab3280a493))
- LPD-78996 @clayui/nav VerticalNav should allow stacked navs with no indent ([d824c08](https://github.com/liferay/liferay-portal/commit/d824c08a994b3babfd22902dcb739a9f0ed11e26))
- LPD-78996 @clayui/core VerticalNav add example without indent ([60adeec](https://github.com/liferay/liferay-portal/commit/60adeec5ac228a6ae1e7a39042c3770a6c0c9a9d))
- LPD-79543 Allow side panel to disable the escape key behavior ([1aa3734](https://github.com/liferay/liferay-portal/commit/1aa3734ee319c84899b114eef93587bdf0fd9658))
- LPD-66630 Update size to small ([096b025](https://github.com/liferay/liferay-portal/commit/096b02530c74447b42f14aa7383aeef733559b6f))
- LPD-66630 Update snapshots ([4f5e77b](https://github.com/liferay/liferay-portal/commit/4f5e77b42757bc9a0d4df11a6a6826156bfaa976))
- LPD-79219 @clayui/core Treeview drag handler should be 24px x 24px when focused ([2cc084a](https://github.com/liferay/liferay-portal/commit/2cc084ae2a760b05df962eff3aff1e9b920383ba))
- LPD-75776 Fix typo ([9d38ea9](https://github.com/liferay/liferay-portal/commit/9d38ea9f9a3745e95d519a66ece726ad13fcf40d))
- LPD-81627 Make symbol optional ([2343e95](https://github.com/liferay/liferay-portal/commit/2343e95f6dc7d6d4f0d3fd6b2d4b1220b2002291))
- LPD-82602 Fix keyboard events ([cde7716](https://github.com/liferay/liferay-portal/commit/cde7716b174daa483aa7724e28846ccc94eb246c))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-82603 Fix escape key input in skipped side panel test ([e0029ee](https://github.com/liferay/liferay-portal/commit/e0029eec3a6084e0a952a4f4e5f7c1fb66e59e21))
- LPD-80239 Add FocusTrap to the dropdown menu ([0a906b5](https://github.com/liferay/liferay-portal/commit/0a906b542b5475edf2e05f4ed04eaf61dd23967b))
- LPD-85711 Add opt-in search capability to the Picker component ([f14c1f9](https://github.com/liferay/liferay-portal/commit/f14c1f94459d7a0876c7a5dfa51bf8df6b328daf))
- LPD-85711 Add test coverage to Picker search capability ([8201b1e](https://github.com/liferay/liferay-portal/commit/8201b1e67ea5acfd5ce9d292a022ec518b041d79))
- LPD-85711 Update Picker documentation to include the new search capability ([d465000](https://github.com/liferay/liferay-portal/commit/d4650005e444fb892fd2af4dfa1bab4ae9634e73))
- LPD-85711 Refactor Picker component to modularize search logic ([4c7178e](https://github.com/liferay/liferay-portal/commit/4c7178e991a4abe6dd5b843f3f80f23bf6ec8ef3))
- LPD-85711 Improve search accessibility ([a8a06e0](https://github.com/liferay/liferay-portal/commit/a8a06e0f6a5a61196a6dec4ead16716f5d423720))
- LPD-85711 Reorganize props ([1fca9cd](https://github.com/liferay/liferay-portal/commit/1fca9cd03e891910f79f92a26491d3f2ce42c242))
- LPD-84613 Move the usePanelWidthMax hook to clay-shared and rename it to useObservedMaxWidth ([6f6c2a7](https://github.com/liferay/liferay-portal/commit/6f6c2a771008635ed912dda53edf72240fdae6aa))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-85564 Add onItemActivate prop and default Enter behavior ([103eb11](https://github.com/liferay/liferay-portal/commit/103eb11ddbad83d7d93cb17cb72eb282ac0bfe2c))
- LPD-85564 Add tests ([c7be8b1](https://github.com/liferay/liferay-portal/commit/c7be8b1e18a5dfca19d8a8969af2a9aaf4b24242))
- LPD-38804 Add type=button to ClayPicker options ([cb6c140](https://github.com/liferay/liferay-portal/commit/cb6c14083e24ed07eb3170a85a278175959cd2cf))
- LPD-38804 Keep selected item label during search ([25a0b52](https://github.com/liferay/liferay-portal/commit/25a0b52c4ca1685de603750adb462eb09d4bc3cc))
- LPD-74518 Scroll the selected option into view when opening Picker ([de93377](https://github.com/liferay/liferay-portal/commit/de93377f714692bffb044553db021496d8e07901))
- LPD-74518 Compute scroll offset via getBoundingClientRect ([380ba94](https://github.com/liferay/liferay-portal/commit/380ba94749e53ce55c082ce1caea03820713c3e4))
- LPD-74518 Scope item lookup to listRef and simplify key fallback ([be03e9f](https://github.com/liferay/liferay-portal/commit/be03e9fe3a4ced90c458b3c741ec8d3f0c80690b))
- LPD-40038 @clayui/core add KeyboardArrowsIndicator ([79010f2](https://github.com/liferay/liferay-portal/commit/79010f2ec8d9e13f092e088cd161b37c32892a06))
- LPD-88290 Move PanelResizer to clay-core ([ddde603](https://github.com/liferay/liferay-portal/commit/ddde603ae4e42d51ceb23cbbd8a117f42e0662cc))
- LPD-88290 Rename PanelResizer by ResizeHandle ([d2453ea](https://github.com/liferay/liferay-portal/commit/d2453ea7bf7321cdcf04b9681cca1ca2447074d0))
- LPD-88290 Adapt imports ([04a0ea2](https://github.com/liferay/liferay-portal/commit/04a0ea2f7b0fa654e3ffe6cdc1c602dac1ab897b))
- LPD-88290 Rename component props and decouple from dependent component ([6636802](https://github.com/liferay/liferay-portal/commit/66368028e373ba1ace49ec8bc036a0856bc46f7d))
- LPD-88290 New styles for the resize handle ([c66e7cb](https://github.com/liferay/liferay-portal/commit/c66e7cbfd58c4a5b0b911d8b8fdfdaf84e579861))
- LPD-88747 Add storybook ([c34b520](https://github.com/liferay/liferay-portal/commit/c34b52058d1d38b409d2ca1c37d724412dd231ca))
- LPD-88746 Add documentation ([c1d7c3e](https://github.com/liferay/liferay-portal/commit/c1d7c3ea984d714823126abe9a119f1847e24112))
- LPD-88290 Add RTL compatibility ([af2828c](https://github.com/liferay/liferay-portal/commit/af2828cb9f4f6b354a34110e1664623f5e3bf1cf))
- LPD-87534 Fixed overlapping items on first open of virtualized tags dropdown ([2b1b71e](https://github.com/liferay/liferay-portal/commit/2b1b71e2d2e53b00bed03ebffe611cc0b5fc8e36))
- LPD-87534 Added Tests ([a822a0f](https://github.com/liferay/liferay-portal/commit/a822a0f12d1d3db26cb200e918905bf1575017ab))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/css

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-75997 @clayui/css Update product-menu-open.svg ([1db5459](https://github.com/liferay/liferay-portal/commit/1db545935c65cc6ca37ae37b57f27951d42405f6))
- LPD-79073 Control Menu button icons should be 16px ([36a0f8a](https://github.com/liferay/liferay-portal/commit/36a0f8adaba142c08d1ac8c460ed919ee81ff9c9))
- LPD-77928 @clayui/css SF variable files ([1fe4e68](https://github.com/liferay/liferay-portal/commit/1fe4e6817515190e3f41d435bc62f3436ba59c0d))
- LPD-79018 @clayui/css sticker-xxl icon should be 40px ([2fd0409](https://github.com/liferay/liferay-portal/commit/2fd0409656bdda0f9e2f765891de2a45660e4237))
- LPD-80362 @clayui/core Treeview icon angle-down should display larger ([22eb0b3](https://github.com/liferay/liferay-portal/commit/22eb0b3c7b87daa3ee9d61538dad07303d844537))
- LPD-78750 @clayui/css adds rocket.svg ([340f130](https://github.com/liferay/liferay-portal/commit/340f13097d6f9f3884532d483edaf83b5c6dd3f3))
- LPD-79219 @clayui/core Treeview drag handler should be 24px x 24px when focused ([2cc084a](https://github.com/liferay/liferay-portal/commit/2cc084ae2a760b05df962eff3aff1e9b920383ba))
- LPD-80374 @clayui/css add icons calculator, price-list, product-configuration, and products ([cffbeff](https://github.com/liferay/liferay-portal/commit/cffbeff59e3509c2b132969f49d4d5ce1898c232))
- LPD-80374 SF icons.svg ([c27b09c](https://github.com/liferay/liferay-portal/commit/c27b09c03777ee017fe66d7ebd5a93e66ff4e3fc))
- LPD-77810 @clayui/css Replace Sass math with CSS calc ([75730ce](https://github.com/liferay/liferay-portal/commit/75730ce27d3621b0c73c5256be4568891d44eec8))
- LPD-77810 @clayui/css Replace math-sign with CSS calc ([42a8656](https://github.com/liferay/liferay-portal/commit/42a865608c05a36c2d2fd5b313b733a8d0709726))
- LPD-77810 @clayui/css SF ([4eb1545](https://github.com/liferay/liferay-portal/commit/4eb154584aeb8bdbd82038223b5a719b81769282))
- LPD-80439 @clayui/css add layout-new-window.svg ([2310a7f](https://github.com/liferay/liferay-portal/commit/2310a7f5800515f39300f77362ccf8415d202c0f))
- LPD-79381 Scope cursor and hover effects to interactive elements ([8ae76f2](https://github.com/liferay/liferay-portal/commit/8ae76f2912116397bdad8bade5e7c8e6faa7c26c))
- LPD-81799 - Regen ([e577af8](https://github.com/liferay/liferay-portal/commit/e577af8942abc4abfc9811ba91781239cf08ef6d))
- LPD-81799 - SF ([3a6f4e5](https://github.com/liferay/liferay-portal/commit/3a6f4e5c95e8e68de19e2902eb8b673311a241c2))
- LPD-80549 @clayui/css clay-css mixin should support any CSS property ([49fc8b8](https://github.com/liferay/liferay-portal/commit/49fc8b8ad1a059fad8facc1f5e164c1373466270))
- LPD-81799 clayui.com should use local version of icons.svg in dev mode ([d57b27c](https://github.com/liferay/liferay-portal/commit/d57b27cec0cf0098114a233eced849776d0d9cd5))
- LPD-81820 @clayui/css add unit to 0 values to make it work with calc() ([9a5ffbf](https://github.com/liferay/liferay-portal/commit/9a5ffbffbb75908bc23dae70b177bcf434507fab))
- LPD-82273 @clayui/css remove use of Sass nth function with variables that could contain a CSS variable ([503c60e](https://github.com/liferay/liferay-portal/commit/503c60e225c5a2b223c7476fc007325d86172433))
- LPD-77641 @clayui/css $custom-control-indicator-checked-color should propagate to checkbox and radio ([02fe938](https://github.com/liferay/liferay-portal/commit/02fe938e328af2c1a472d06ae8ab20d55d018242))
- LPD-83068 @clayui/css add books-brush.svg ([3d3f1b2](https://github.com/liferay/liferay-portal/commit/3d3f1b221cf72464d7f28ec9ea0c907c8775cf31))
- LPD-71471 @clayui/css clay-data-label-text-position function shouldn't error with CSS variables ([a88e120](https://github.com/liferay/liferay-portal/commit/a88e1203878e72258f746b31633cfd03b3666090))
- LPD-71471 @clayui/css Use calc for $spacers ([3a8d947](https://github.com/liferay/liferay-portal/commit/3a8d9476d2c67482bc535ebf16aad92fa46f9f7a))
- LPD-71471 @clayui/css Add Atlas custom properties theme ([868a5cf](https://github.com/liferay/liferay-portal/commit/868a5cf310d35d0ea06ade8e0882057710af3028))
- LPD-81854 @clayui/css atlas variable import order should match base variable import order ([1c52c57](https://github.com/liferay/liferay-portal/commit/1c52c57b0f9427edea79b6116ec9dc2d0a2a87df))
- LPD-86312 @clayui/css remove $enable-atlas-custom-properties, atlas-custom-properties should be manually imported ([b62b47f](https://github.com/liferay/liferay-portal/commit/b62b47fd3f7ab9dc1081c961890c06cc29b4e161))
- LPD-66974 Add focus ring animation ([1e1cfc1](https://github.com/liferay/liferay-portal/commit/1e1cfc13bb7f2e92b5a6582c443a1418337cf1a0))
- LPD-66974 Add focus ring animation for atlas-custom-properties ([b65ded6](https://github.com/liferay/liferay-portal/commit/b65ded6af7754d950e3f65c89e68716d2b39bab2))
- LPD-66973 Retrigger focus ring animation on browser tab return ([886d229](https://github.com/liferay/liferay-portal/commit/886d2292438211b68c328c729542e23d1319bb67))
- LPD-66973 Avoid animation conflict and improve tab returning effect ([9f24b66](https://github.com/liferay/liferay-portal/commit/9f24b66a91eef63489c5ab44fd762ab3735f8bd1))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-86387 Update Clay versions across modules ([8cd5677](https://github.com/liferay/liferay-portal/commit/8cd5677d5089f81244b4e390b4cd300cbf8aedf9))
- LPD-78369 Scale label font-size and line-height inside form-group-sm ([cdf2ffa](https://github.com/liferay/liferay-portal/commit/cdf2ffae847ba432c9f5651e13ab769c9db05178))
- LPD-78369 Add atlas-custom-properties variables and refactor test assertions ([fbaa442](https://github.com/liferay/liferay-portal/commit/fbaa442762bcfebbc2293b3225dcd697b676176e))
- LPD-73275 Use map-deep-merge for breadcrumbs ([a27b889](https://github.com/liferay/liferay-portal/commit/a27b889928aefed5e145d39096190a5d6b8239d8))
- LPD-81847 Show cadmin focus shadow on DualListBox selects ([75f0156](https://github.com/liferay/liferay-portal/commit/75f0156f47b3306e86e54ff0fbd1a01a39e2a1e5))
- LPD-81847 Show atlas focus shadow on DualListBox selects ([79428fb](https://github.com/liferay/liferay-portal/commit/79428fb3543810b1805c0beed451192b6715a7f7))
- LPD-81847 Show atlas custom properties focus shadow on DualListBox selects ([316be96](https://github.com/liferay/liferay-portal/commit/316be9666fa58c4893054510a20b12e3ebfbd1f4))
- LPD-81847 SF ([29b8d6f](https://github.com/liferay/liferay-portal/commit/29b8d6fba6010e2ad04e173275ea5fe368f9536d))
- LPD-77874 Anchor breadcrumb collapse toggle to first row of items ([94bf1bf](https://github.com/liferay/liferay-portal/commit/94bf1bf19bd38e8592963482b7f3853bd318327b))
- LPD-77874 Fix breadcrumb toggle click area and icon alignment ([57a90c0](https://github.com/liferay/liferay-portal/commit/57a90c0f2944c95067a5dadc61342a4fa7ba2c47))
- LPD-77874 SF ([e88e459](https://github.com/liferay/liferay-portal/commit/e88e4598f58142cc9f7037106c5aa65c91ecedfa))
- LPD-44626 Reset descendant letter-spacing on menubar-transparent links ([f30c8cf](https://github.com/liferay/liferay-portal/commit/f30c8cf592329110ac6af1eb5894abbb4ac4f171))
- LPD-88394 Prevent focus ring from displacing toggle-switch ([d108807](https://github.com/liferay/liferay-portal/commit/d108807a4277c4765d541657f731b07009b647f2))
- LPD-88394 Fix focus ring animation ([a1469eb](https://github.com/liferay/liferay-portal/commit/a1469eb0909977c97fef5c6349d71185823acdf9))
- LPD-40038 @clayui/css add arrow-key-{up,down,left,right}.svg ([be98ebd](https://github.com/liferay/liferay-portal/commit/be98ebdc4af4ec80bc65dfe22d794f03422a55a3))
- LPD-40038 @clayui/core add KeyboardArrowsIndicator ([79010f2](https://github.com/liferay/liferay-portal/commit/79010f2ec8d9e13f092e088cd161b37c32892a06))
- LPD-88290 New styles for the resize handle ([c66e7cb](https://github.com/liferay/liferay-portal/commit/c66e7cbfd58c4a5b0b911d8b8fdfdaf84e579861))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/data-provider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82604 Fix clay-data-provider test with CacheFirst, it was expecting the cache to be already populated ([38fb06d](https://github.com/liferay/liferay-portal/commit/38fb06d2913f0b985ae53010ff60de76601ad0c8))
- LPD-82604 Fix DataClient, which didn't provide a method to clear completed promises, breaking the CacheAndNetwork fetch policy ([b99f481](https://github.com/liferay/liferay-portal/commit/b99f481a35f2da518491229e6a78607bd6920213))
- LPD-82604 Fix clay-data-provider test with CacheAndNetwork, it was expecting the cache to be already populated ([337d076](https://github.com/liferay/liferay-portal/commit/337d07695378423d84cd4aace237df41cbf49cca))
- LPD-82604 Fix clay-data-provider test using Node.js-specific unref method, adapting it to reflect the browser environment ([a540d95](https://github.com/liferay/liferay-portal/commit/a540d95ebaffac1d8b31b5d53741ab2e88a8add4))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/date-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/drop-down

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-82700 Remove aria-labelledby for DropDown groups without header ([dd8f061](https://github.com/liferay/liferay-portal/commit/dd8f061b49d3a731d14a2e101e6abed533ff135b))
- LPD-83370 Enable DropDownWithDrilldown skipped tests ([aefacb2](https://github.com/liferay/liferay-portal/commit/aefacb276312ec759f3554e41b37f04932b1f067))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/empty-state

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/form

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-80237 Allow passing refs to the Clay Select ([55e2649](https://github.com/liferay/liferay-portal/commit/55e2649bdc0aef25991539f1d4fa9c4e4a212645))
- LPD-83371 Unskip test. Use the callback function to test the multiple options ([c13571d](https://github.com/liferay/liferay-portal/commit/c13571d350579998edfa8aa6228f778f8d81e74a))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/icon

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78750 @clayui.com add rocket.svg to docs ([e74d242](https://github.com/liferay/liferay-portal/commit/e74d2429a8f8c1b6fc1e328bd68ff82322f8fc22))
- LPD-80374 clayui.com add aliases for calculator, price-list, product-configuration and products icons ([c47704a](https://github.com/liferay/liferay-portal/commit/c47704a0a6032017996175da941ab6062fed9ff9))
- LPD-80439 clayui.com document layout-new-window.svg ([7422fbf](https://github.com/liferay/liferay-portal/commit/7422fbfd99740a28d6c088cddec5dcaf3fa77652))
- LPD-83068 clayui.com add books-brush.svg and aliases ([5e950e1](https://github.com/liferay/liferay-portal/commit/5e950e19fe9fa530ee36a2813a6d4ce8f06b31ef))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-40038 clayui.com add arrow-key-* and aliases ([9d2c4fd](https://github.com/liferay/liferay-portal/commit/9d2c4fd657bc19a56cdc288c5e71887757f4be94))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/label

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/layout

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/link

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/list

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/loading-indicator

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/localized-input

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/management-toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/modal

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79663 Only render items if elements are provided ([20f3b19](https://github.com/liferay/liferay-portal/commit/20f3b193ef3a1cf1d5ee43a8a7bcaaee4bcd2dd2))
- LPD-79663 Add test ([d5d832d](https://github.com/liferay/liferay-portal/commit/d5d832d6c11e94cacbb78973c93443fe8e10906d))
- LPD-79663 Update snapshots ([b4906d5](https://github.com/liferay/liferay-portal/commit/b4906d5be205e68db99cb415e6da2ff850ea59d8))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/multi-select

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83372 Enable skipped clay-multi-select tests ([3764509](https://github.com/liferay/liferay-portal/commit/3764509a95729468bc1054782d6a9a406b25b665))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-87534 Update test snapshots ([63e0b4a](https://github.com/liferay/liferay-portal/commit/63e0b4a9515f297790345f61717ce2841a79bf75))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/multi-step-nav

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79381 Add as prop to Indicator to support custom tags ([626fe1a](https://github.com/liferay/liferay-portal/commit/626fe1acdc2106be3690f87c63820036b2a30511))
- LPD-79381 Add buttonProps and disabled support to MultiStepNav Indicator ([be6b4d5](https://github.com/liferay/liferay-portal/commit/be6b4d576a807c53d0dd85714b0ddbe4ce92cb86))
- LPD-79381 Add disabled props to the step Item ([cc280ca](https://github.com/liferay/liferay-portal/commit/cc280ca56bd641c07359db4be0e904cd4eff9e0b))
- LPD-79381 Add Disabled and NonInteractive stories to MultiStepNav ([d5db4bc](https://github.com/liferay/liferay-portal/commit/d5db4bc68041990beea9d0eac0a6633af135bb72))
- LPD-79381 Remove deprecated complete prop from MultiStepNav stories ([cbfed5f](https://github.com/liferay/liferay-portal/commit/cbfed5fd56bd3c52a688fd66fd4cf09c502ea1a7))
- LPD-79381 Respect 'as' prop and replace buttonProps with elementProps ([ffe7a0a](https://github.com/liferay/liferay-portal/commit/ffe7a0afabedbab5b236483bab2c30a0d5b2c58e))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/nav

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78996 @clayui/nav VerticalNav should allow stacked navs with no indent ([d824c08](https://github.com/liferay/liferay-portal/commit/d824c08a994b3babfd22902dcb739a9f0ed11e26))
- LPD-82605 Remove skip from tests, they pass ([228e368](https://github.com/liferay/liferay-portal/commit/228e3681671e3f3907f714b2f96b96dc15b9c0fd))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/navigation-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/pagination

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83387 Add global library ([b9fd33b](https://github.com/liferay/liferay-portal/commit/b9fd33b2f37a3f52c130ece8e208d1f7e0941b56))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/pagination-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/panel

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/popover

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-85584 Fix focus not returning to trigger after popover blur | When navigating via keyboard, focus moves to the popover when it opens. If a blur event occurs, focus is lost instead of returning to the previous element. This fix ensures that focus returns to the trigger element. ([29d35b0](https://github.com/liferay/liferay-portal/commit/29d35b01cf9bd77033f454e45d041493b1202b47))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/progress-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79357 Add classname to progress bar ([cab0a14](https://github.com/liferay/liferay-portal/commit/cab0a14942e3ec5c95ece4f8671e51839297f65e))
- LPD-79357 @clayui/progress-bar adds fillBarClassName example in Storybook ([5130f70](https://github.com/liferay/liferay-portal/commit/5130f706f8c16440be3edece982c6f4f53e10455))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/provider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82604 Fix DataClient, which didn't provide a method to clear completed promises, breaking the CacheAndNetwork fetch policy ([b99f481](https://github.com/liferay/liferay-portal/commit/b99f481a35f2da518491229e6a78607bd6920213))
- LPD-66973 Retrigger focus ring animation on browser tab return ([886d229](https://github.com/liferay/liferay-portal/commit/886d2292438211b68c328c729542e23d1319bb67))
- LPD-66973 Avoid animation conflict and improve tab returning effect ([9f24b66](https://github.com/liferay/liferay-portal/commit/9f24b66a91eef63489c5ab44fd762ab3735f8bd1))
- LPD-66973 Use constants to avoid CSS class repetition ([565d0a2](https://github.com/liferay/liferay-portal/commit/565d0a26c78727edc74d538262fc66f8b07ebbc6))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/shared

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82606 Use fireEvent. userEvent was triggering keyups between ArrowRight events so the last part about acceleration was never working ([0c313b9](https://github.com/liferay/liferay-portal/commit/0c313b9533b46e30ab078777b03b5e45dc9b4b6b))
- LPD-84613 Move the usePanelWidthMax hook to clay-shared and rename it to useObservedMaxWidth ([6f6c2a7](https://github.com/liferay/liferay-portal/commit/6f6c2a771008635ed912dda53edf72240fdae6aa))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-69776 Rename useOverlayPositon to useOverlayPosition ([7152aae](https://github.com/liferay/liferay-portal/commit/7152aae19e8be4380d785f0c2bd7610923e3306c))
- LPD-69776 Reset sourceElement positioning ([435b602](https://github.com/liferay/liferay-portal/commit/435b602a0f282ec1b77267edd4eff65827d48214))
- LPD-69776 Add test coverage to doAlign positioning reset ([b137c44](https://github.com/liferay/liferay-portal/commit/b137c44f0976324e171159adcbad5aa7abe08851))
- LPD-88290 Move PanelResizer to clay-core ([ddde603](https://github.com/liferay/liferay-portal/commit/ddde603ae4e42d51ceb23cbbd8a117f42e0662cc))
- LPD-88290 Rename PanelResizer by ResizeHandle ([d2453ea](https://github.com/liferay/liferay-portal/commit/d2453ea7bf7321cdcf04b9681cca1ca2447074d0))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/slider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/sticker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78752 Add product icon ([5f5fb3f](https://github.com/liferay/liferay-portal/commit/5f5fb3fc32d41276098bfd249fd0080f46ed062c))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/table

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/tabs

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83389 Remove tests related to modern and displayType props for ClayTabs since these attributes are not supported anymore and not used by the List component since Apr 10 2023 ([0cfc6a7](https://github.com/liferay/liferay-portal/commit/0cfc6a77f01c76e7e72fd42cc132bb23ea0c5c55))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/time-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/tooltip

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

## @clayui/upper-toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-89587 Bump Clay versions to 3.162.0 ([3eaf9a8](https://github.com/liferay/liferay-portal/commit/3eaf9a8a13c95f63fffd9b38996001879beefb13))

# [3.162.0] (2026-05-11)

## @clayui/alert

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-81627 Allow passing classNames to the container ([1f9f4da](https://github.com/liferay/liferay-portal/commit/1f9f4da502edcfdd557a19645e383624cf9bb1fc))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/autocomplete

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82600 Fix Escape key input ([e02bcac](https://github.com/liferay/liferay-portal/commit/e02bcac12cbca7dd8d379f2b6a1ab81928aab56b))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/badge

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83029 Allow passing icon to badge ([26ceeb3](https://github.com/liferay/liferay-portal/commit/26ceeb3731afc917d0621aea45d967f16d2df0e5))
- LPD-83029 Add test ([ffa79b5](https://github.com/liferay/liferay-portal/commit/ffa79b5ed6336425bf6a5e9e06574a78ece42b58))
- LPD-83029 Implement ItemAfter, ItemBefore and ItemExpand logic ([19fcf1f](https://github.com/liferay/liferay-portal/commit/19fcf1f41c0cbb34cefb00bcd51f9cfcce7722b1))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/breadcrumb

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/button

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/card

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-83803 Make ClayCardWithInfo img not draggable ([e6b9674](https://github.com/liferay/liferay-portal/commit/e6b9674a2d9f3ed65f2173fae2c7b0f00ed27940))
- LPD-83803 Update test snapshots ([208ed9b](https://github.com/liferay/liferay-portal/commit/208ed9bde0b51d3e2f1e0b84331394944c607130))

## @clayui/color-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82601 Remove skip, test is working fine ([e0ad6fe](https://github.com/liferay/liferay-portal/commit/e0ad6fee201ce87c88db7b00453a265ee9c35785))
- LPD-80236 Convert index.ts into barrel file for exports ([ca8603a](https://github.com/liferay/liferay-portal/commit/ca8603a4049edfe93d16ad4b4dfa618362e35030))
- LPD-80236 Add useColorPicker hook ([82a441e](https://github.com/liferay/liferay-portal/commit/82a441e1e9a3969ae9e09d19e46607e304707916))
- LPD-80236 Add Field component to isolate the color input, splotch and dropdown ([74de4f2](https://github.com/liferay/liferay-portal/commit/74de4f237c92208742c97a8d25aae7157cb40375))
- LPD-80236 Export useColorPicker hook, Field and Editor components to reuse them ([2b55a42](https://github.com/liferay/liferay-portal/commit/2b55a42394e109edf4e454a4ea8e1cffd01cbe39))
- LPD-80236 Add the ability to add an external trigger reference ([13892d2](https://github.com/liferay/liferay-portal/commit/13892d25edd143ec7204c5c96b6870058ea9993a))
- LPD-80236 Adapt the parseColorValue function to the ClayColorPicker, taking into account the hex6 and hex8 ([c9d85e9](https://github.com/liferay/liferay-portal/commit/c9d85e96be51906c6c8699d95936255486f17418))
- LPD-80236 Save the value when onBlur is done in the Editor hex input ([ab85832](https://github.com/liferay/liferay-portal/commit/ab85832fee860e17e1a6df45b397987055a23059))
- LPD-80236 Improve props and descriptions ([6da7abb](https://github.com/liferay/liferay-portal/commit/6da7abb3a3dc9f6afcbf4eb24febbe592d4af6d9))
- LPD-80236 Rename onClickSplotch to onSplotchClick ([bec76ac](https://github.com/liferay/liferay-portal/commit/bec76acc04c3bbd9ed357adf790094fde7f09e29))
- LPD-80236 Create new function to format the hex colors ([bed726a](https://github.com/liferay/liferay-portal/commit/bed726a2fb3ce44c76a4157ac4adb2c93a77de40))
- LPD-83988 Export all the necessary functions from util.ts ([fdd7b2c](https://github.com/liferay/liferay-portal/commit/fdd7b2c82fcd839f34d5f5f7f29bd27b827c74c4))
- LPD-83988 Update the package types in the color-picker docs ([d2b0084](https://github.com/liferay/liferay-portal/commit/d2b00844c744b7175051336914471d4dd3d19957))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/core

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78752 Allow configuring custom classes in Clay SidePanel.Title ([5626239](https://github.com/liferay/liferay-portal/commit/56262393602378d933b9d7962fc609700b4e8407))
- LPD-79027 Use existing onEnd callback ([d6d039d](https://github.com/liferay/liferay-portal/commit/d6d039d7073cbc6558ca4ab14bc08eb372c035bb))
- LPD-79027 Reset state also after drop with mouse ([03de461](https://github.com/liferay/liferay-portal/commit/03de46132d6ac765df42adc6452da3ab3280a493))
- LPD-78996 @clayui/nav VerticalNav should allow stacked navs with no indent ([d824c08](https://github.com/liferay/liferay-portal/commit/d824c08a994b3babfd22902dcb739a9f0ed11e26))
- LPD-78996 @clayui/core VerticalNav add example without indent ([60adeec](https://github.com/liferay/liferay-portal/commit/60adeec5ac228a6ae1e7a39042c3770a6c0c9a9d))
- LPD-79543 Allow side panel to disable the escape key behavior ([1aa3734](https://github.com/liferay/liferay-portal/commit/1aa3734ee319c84899b114eef93587bdf0fd9658))
- LPD-66630 Update size to small ([096b025](https://github.com/liferay/liferay-portal/commit/096b02530c74447b42f14aa7383aeef733559b6f))
- LPD-66630 Update snapshots ([4f5e77b](https://github.com/liferay/liferay-portal/commit/4f5e77b42757bc9a0d4df11a6a6826156bfaa976))
- LPD-79219 @clayui/core Treeview drag handler should be 24px x 24px when focused ([2cc084a](https://github.com/liferay/liferay-portal/commit/2cc084ae2a760b05df962eff3aff1e9b920383ba))
- LPD-75776 Fix typo ([9d38ea9](https://github.com/liferay/liferay-portal/commit/9d38ea9f9a3745e95d519a66ece726ad13fcf40d))
- LPD-81627 Make symbol optional ([2343e95](https://github.com/liferay/liferay-portal/commit/2343e95f6dc7d6d4f0d3fd6b2d4b1220b2002291))
- LPD-82602 Fix keyboard events ([cde7716](https://github.com/liferay/liferay-portal/commit/cde7716b174daa483aa7724e28846ccc94eb246c))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-82603 Fix escape key input in skipped side panel test ([e0029ee](https://github.com/liferay/liferay-portal/commit/e0029eec3a6084e0a952a4f4e5f7c1fb66e59e21))
- LPD-80239 Add FocusTrap to the dropdown menu ([0a906b5](https://github.com/liferay/liferay-portal/commit/0a906b542b5475edf2e05f4ed04eaf61dd23967b))
- LPD-85711 Add opt-in search capability to the Picker component ([f14c1f9](https://github.com/liferay/liferay-portal/commit/f14c1f94459d7a0876c7a5dfa51bf8df6b328daf))
- LPD-85711 Add test coverage to Picker search capability ([8201b1e](https://github.com/liferay/liferay-portal/commit/8201b1e67ea5acfd5ce9d292a022ec518b041d79))
- LPD-85711 Update Picker documentation to include the new search capability ([d465000](https://github.com/liferay/liferay-portal/commit/d4650005e444fb892fd2af4dfa1bab4ae9634e73))
- LPD-85711 Refactor Picker component to modularize search logic ([4c7178e](https://github.com/liferay/liferay-portal/commit/4c7178e991a4abe6dd5b843f3f80f23bf6ec8ef3))
- LPD-85711 Improve search accessibility ([a8a06e0](https://github.com/liferay/liferay-portal/commit/a8a06e0f6a5a61196a6dec4ead16716f5d423720))
- LPD-85711 Reorganize props ([1fca9cd](https://github.com/liferay/liferay-portal/commit/1fca9cd03e891910f79f92a26491d3f2ce42c242))
- LPD-84613 Move the usePanelWidthMax hook to clay-shared and rename it to useObservedMaxWidth ([6f6c2a7](https://github.com/liferay/liferay-portal/commit/6f6c2a771008635ed912dda53edf72240fdae6aa))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-85564 Add onItemActivate prop and default Enter behavior ([103eb11](https://github.com/liferay/liferay-portal/commit/103eb11ddbad83d7d93cb17cb72eb282ac0bfe2c))
- LPD-85564 Add tests ([c7be8b1](https://github.com/liferay/liferay-portal/commit/c7be8b1e18a5dfca19d8a8969af2a9aaf4b24242))
- LPD-38804 Add type=button to ClayPicker options ([cb6c140](https://github.com/liferay/liferay-portal/commit/cb6c14083e24ed07eb3170a85a278175959cd2cf))
- LPD-38804 Keep selected item label during search ([25a0b52](https://github.com/liferay/liferay-portal/commit/25a0b52c4ca1685de603750adb462eb09d4bc3cc))
- LPD-74518 Scroll the selected option into view when opening Picker ([de93377](https://github.com/liferay/liferay-portal/commit/de93377f714692bffb044553db021496d8e07901))
- LPD-74518 Compute scroll offset via getBoundingClientRect ([380ba94](https://github.com/liferay/liferay-portal/commit/380ba94749e53ce55c082ce1caea03820713c3e4))
- LPD-74518 Scope item lookup to listRef and simplify key fallback ([be03e9f](https://github.com/liferay/liferay-portal/commit/be03e9fe3a4ced90c458b3c741ec8d3f0c80690b))
- LPD-40038 @clayui/core add KeyboardArrowsIndicator ([79010f2](https://github.com/liferay/liferay-portal/commit/79010f2ec8d9e13f092e088cd161b37c32892a06))
- LPD-88290 Move PanelResizer to clay-core ([ddde603](https://github.com/liferay/liferay-portal/commit/ddde603ae4e42d51ceb23cbbd8a117f42e0662cc))
- LPD-88290 Rename PanelResizer by ResizeHandle ([d2453ea](https://github.com/liferay/liferay-portal/commit/d2453ea7bf7321cdcf04b9681cca1ca2447074d0))
- LPD-88290 Adapt imports ([04a0ea2](https://github.com/liferay/liferay-portal/commit/04a0ea2f7b0fa654e3ffe6cdc1c602dac1ab897b))
- LPD-88290 Rename component props and decouple from dependent component ([6636802](https://github.com/liferay/liferay-portal/commit/66368028e373ba1ace49ec8bc036a0856bc46f7d))
- LPD-88290 New styles for the resize handle ([c66e7cb](https://github.com/liferay/liferay-portal/commit/c66e7cbfd58c4a5b0b911d8b8fdfdaf84e579861))
- LPD-88747 Add storybook ([c34b520](https://github.com/liferay/liferay-portal/commit/c34b52058d1d38b409d2ca1c37d724412dd231ca))
- LPD-88746 Add documentation ([c1d7c3e](https://github.com/liferay/liferay-portal/commit/c1d7c3ea984d714823126abe9a119f1847e24112))
- LPD-88290 Add RTL compatibility ([af2828c](https://github.com/liferay/liferay-portal/commit/af2828cb9f4f6b354a34110e1664623f5e3bf1cf))

## @clayui/css

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-75997 @clayui/css Update product-menu-open.svg ([1db5459](https://github.com/liferay/liferay-portal/commit/1db545935c65cc6ca37ae37b57f27951d42405f6))
- LPD-79073 Control Menu button icons should be 16px ([36a0f8a](https://github.com/liferay/liferay-portal/commit/36a0f8adaba142c08d1ac8c460ed919ee81ff9c9))
- LPD-77928 @clayui/css SF variable files ([1fe4e68](https://github.com/liferay/liferay-portal/commit/1fe4e6817515190e3f41d435bc62f3436ba59c0d))
- LPD-79018 @clayui/css sticker-xxl icon should be 40px ([2fd0409](https://github.com/liferay/liferay-portal/commit/2fd0409656bdda0f9e2f765891de2a45660e4237))
- LPD-80362 @clayui/core Treeview icon angle-down should display larger ([22eb0b3](https://github.com/liferay/liferay-portal/commit/22eb0b3c7b87daa3ee9d61538dad07303d844537))
- LPD-78750 @clayui/css adds rocket.svg ([340f130](https://github.com/liferay/liferay-portal/commit/340f13097d6f9f3884532d483edaf83b5c6dd3f3))
- LPD-79219 @clayui/core Treeview drag handler should be 24px x 24px when focused ([2cc084a](https://github.com/liferay/liferay-portal/commit/2cc084ae2a760b05df962eff3aff1e9b920383ba))
- LPD-80374 @clayui/css add icons calculator, price-list, product-configuration, and products ([cffbeff](https://github.com/liferay/liferay-portal/commit/cffbeff59e3509c2b132969f49d4d5ce1898c232))
- LPD-80374 SF icons.svg ([c27b09c](https://github.com/liferay/liferay-portal/commit/c27b09c03777ee017fe66d7ebd5a93e66ff4e3fc))
- LPD-77810 @clayui/css Replace Sass math with CSS calc ([75730ce](https://github.com/liferay/liferay-portal/commit/75730ce27d3621b0c73c5256be4568891d44eec8))
- LPD-77810 @clayui/css Replace math-sign with CSS calc ([42a8656](https://github.com/liferay/liferay-portal/commit/42a865608c05a36c2d2fd5b313b733a8d0709726))
- LPD-77810 @clayui/css SF ([4eb1545](https://github.com/liferay/liferay-portal/commit/4eb154584aeb8bdbd82038223b5a719b81769282))
- LPD-80439 @clayui/css add layout-new-window.svg ([2310a7f](https://github.com/liferay/liferay-portal/commit/2310a7f5800515f39300f77362ccf8415d202c0f))
- LPD-79381 Scope cursor and hover effects to interactive elements ([8ae76f2](https://github.com/liferay/liferay-portal/commit/8ae76f2912116397bdad8bade5e7c8e6faa7c26c))
- LPD-81799 - Regen ([e577af8](https://github.com/liferay/liferay-portal/commit/e577af8942abc4abfc9811ba91781239cf08ef6d))
- LPD-81799 - SF ([3a6f4e5](https://github.com/liferay/liferay-portal/commit/3a6f4e5c95e8e68de19e2902eb8b673311a241c2))
- LPD-80549 @clayui/css clay-css mixin should support any CSS property ([49fc8b8](https://github.com/liferay/liferay-portal/commit/49fc8b8ad1a059fad8facc1f5e164c1373466270))
- LPD-81799 clayui.com should use local version of icons.svg in dev mode ([d57b27c](https://github.com/liferay/liferay-portal/commit/d57b27cec0cf0098114a233eced849776d0d9cd5))
- LPD-81820 @clayui/css add unit to 0 values to make it work with calc() ([9a5ffbf](https://github.com/liferay/liferay-portal/commit/9a5ffbffbb75908bc23dae70b177bcf434507fab))
- LPD-82273 @clayui/css remove use of Sass nth function with variables that could contain a CSS variable ([503c60e](https://github.com/liferay/liferay-portal/commit/503c60e225c5a2b223c7476fc007325d86172433))
- LPD-77641 @clayui/css $custom-control-indicator-checked-color should propagate to checkbox and radio ([02fe938](https://github.com/liferay/liferay-portal/commit/02fe938e328af2c1a472d06ae8ab20d55d018242))
- LPD-83068 @clayui/css add books-brush.svg ([3d3f1b2](https://github.com/liferay/liferay-portal/commit/3d3f1b221cf72464d7f28ec9ea0c907c8775cf31))
- LPD-71471 @clayui/css clay-data-label-text-position function shouldn't error with CSS variables ([a88e120](https://github.com/liferay/liferay-portal/commit/a88e1203878e72258f746b31633cfd03b3666090))
- LPD-71471 @clayui/css Use calc for $spacers ([3a8d947](https://github.com/liferay/liferay-portal/commit/3a8d9476d2c67482bc535ebf16aad92fa46f9f7a))
- LPD-71471 @clayui/css Add Atlas custom properties theme ([868a5cf](https://github.com/liferay/liferay-portal/commit/868a5cf310d35d0ea06ade8e0882057710af3028))
- LPD-81854 @clayui/css atlas variable import order should match base variable import order ([1c52c57](https://github.com/liferay/liferay-portal/commit/1c52c57b0f9427edea79b6116ec9dc2d0a2a87df))
- LPD-86312 @clayui/css remove $enable-atlas-custom-properties, atlas-custom-properties should be manually imported ([b62b47f](https://github.com/liferay/liferay-portal/commit/b62b47fd3f7ab9dc1081c961890c06cc29b4e161))
- LPD-66974 Add focus ring animation ([1e1cfc1](https://github.com/liferay/liferay-portal/commit/1e1cfc13bb7f2e92b5a6582c443a1418337cf1a0))
- LPD-66974 Add focus ring animation for atlas-custom-properties ([b65ded6](https://github.com/liferay/liferay-portal/commit/b65ded6af7754d950e3f65c89e68716d2b39bab2))
- LPD-66973 Retrigger focus ring animation on browser tab return ([886d229](https://github.com/liferay/liferay-portal/commit/886d2292438211b68c328c729542e23d1319bb67))
- LPD-66973 Avoid animation conflict and improve tab returning effect ([9f24b66](https://github.com/liferay/liferay-portal/commit/9f24b66a91eef63489c5ab44fd762ab3735f8bd1))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-86387 Update Clay versions across modules ([8cd5677](https://github.com/liferay/liferay-portal/commit/8cd5677d5089f81244b4e390b4cd300cbf8aedf9))
- LPD-78369 Scale label font-size and line-height inside form-group-sm ([cdf2ffa](https://github.com/liferay/liferay-portal/commit/cdf2ffae847ba432c9f5651e13ab769c9db05178))
- LPD-78369 Add atlas-custom-properties variables and refactor test assertions ([fbaa442](https://github.com/liferay/liferay-portal/commit/fbaa442762bcfebbc2293b3225dcd697b676176e))
- LPD-73275 Use map-deep-merge for breadcrumbs ([a27b889](https://github.com/liferay/liferay-portal/commit/a27b889928aefed5e145d39096190a5d6b8239d8))
- LPD-81847 Show cadmin focus shadow on DualListBox selects ([75f0156](https://github.com/liferay/liferay-portal/commit/75f0156f47b3306e86e54ff0fbd1a01a39e2a1e5))
- LPD-81847 Show atlas focus shadow on DualListBox selects ([79428fb](https://github.com/liferay/liferay-portal/commit/79428fb3543810b1805c0beed451192b6715a7f7))
- LPD-81847 Show atlas custom properties focus shadow on DualListBox selects ([316be96](https://github.com/liferay/liferay-portal/commit/316be9666fa58c4893054510a20b12e3ebfbd1f4))
- LPD-81847 SF ([29b8d6f](https://github.com/liferay/liferay-portal/commit/29b8d6fba6010e2ad04e173275ea5fe368f9536d))
- LPD-77874 Anchor breadcrumb collapse toggle to first row of items ([94bf1bf](https://github.com/liferay/liferay-portal/commit/94bf1bf19bd38e8592963482b7f3853bd318327b))
- LPD-77874 Fix breadcrumb toggle click area and icon alignment ([57a90c0](https://github.com/liferay/liferay-portal/commit/57a90c0f2944c95067a5dadc61342a4fa7ba2c47))
- LPD-77874 SF ([e88e459](https://github.com/liferay/liferay-portal/commit/e88e4598f58142cc9f7037106c5aa65c91ecedfa))
- LPD-44626 Reset descendant letter-spacing on menubar-transparent links ([f30c8cf](https://github.com/liferay/liferay-portal/commit/f30c8cf592329110ac6af1eb5894abbb4ac4f171))
- LPD-88394 Prevent focus ring from displacing toggle-switch ([d108807](https://github.com/liferay/liferay-portal/commit/d108807a4277c4765d541657f731b07009b647f2))
- LPD-88394 Fix focus ring animation ([a1469eb](https://github.com/liferay/liferay-portal/commit/a1469eb0909977c97fef5c6349d71185823acdf9))
- LPD-40038 @clayui/css add arrow-key-{up,down,left,right}.svg ([be98ebd](https://github.com/liferay/liferay-portal/commit/be98ebdc4af4ec80bc65dfe22d794f03422a55a3))
- LPD-40038 @clayui/core add KeyboardArrowsIndicator ([79010f2](https://github.com/liferay/liferay-portal/commit/79010f2ec8d9e13f092e088cd161b37c32892a06))
- LPD-88290 New styles for the resize handle ([c66e7cb](https://github.com/liferay/liferay-portal/commit/c66e7cbfd58c4a5b0b911d8b8fdfdaf84e579861))

## @clayui/data-provider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82604 Fix clay-data-provider test with CacheFirst, it was expecting the cache to be already populated ([38fb06d](https://github.com/liferay/liferay-portal/commit/38fb06d2913f0b985ae53010ff60de76601ad0c8))
- LPD-82604 Fix DataClient, which didn't provide a method to clear completed promises, breaking the CacheAndNetwork fetch policy ([b99f481](https://github.com/liferay/liferay-portal/commit/b99f481a35f2da518491229e6a78607bd6920213))
- LPD-82604 Fix clay-data-provider test with CacheAndNetwork, it was expecting the cache to be already populated ([337d076](https://github.com/liferay/liferay-portal/commit/337d07695378423d84cd4aace237df41cbf49cca))
- LPD-82604 Fix clay-data-provider test using Node.js-specific unref method, adapting it to reflect the browser environment ([a540d95](https://github.com/liferay/liferay-portal/commit/a540d95ebaffac1d8b31b5d53741ab2e88a8add4))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/date-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/drop-down

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-82700 Remove aria-labelledby for DropDown groups without header ([dd8f061](https://github.com/liferay/liferay-portal/commit/dd8f061b49d3a731d14a2e101e6abed533ff135b))
- LPD-83370 Enable DropDownWithDrilldown skipped tests ([aefacb2](https://github.com/liferay/liferay-portal/commit/aefacb276312ec759f3554e41b37f04932b1f067))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/empty-state

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/form

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-80237 Allow passing refs to the Clay Select ([55e2649](https://github.com/liferay/liferay-portal/commit/55e2649bdc0aef25991539f1d4fa9c4e4a212645))
- LPD-83371 Unskip test. Use the callback function to test the multiple options ([c13571d](https://github.com/liferay/liferay-portal/commit/c13571d350579998edfa8aa6228f778f8d81e74a))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/icon

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78750 @clayui.com add rocket.svg to docs ([e74d242](https://github.com/liferay/liferay-portal/commit/e74d2429a8f8c1b6fc1e328bd68ff82322f8fc22))
- LPD-80374 clayui.com add aliases for calculator, price-list, product-configuration and products icons ([c47704a](https://github.com/liferay/liferay-portal/commit/c47704a0a6032017996175da941ab6062fed9ff9))
- LPD-80439 clayui.com document layout-new-window.svg ([7422fbf](https://github.com/liferay/liferay-portal/commit/7422fbfd99740a28d6c088cddec5dcaf3fa77652))
- LPD-83068 clayui.com add books-brush.svg and aliases ([5e950e1](https://github.com/liferay/liferay-portal/commit/5e950e19fe9fa530ee36a2813a6d4ce8f06b31ef))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-40038 clayui.com add arrow-key-* and aliases ([9d2c4fd](https://github.com/liferay/liferay-portal/commit/9d2c4fd657bc19a56cdc288c5e71887757f4be94))

## @clayui/label

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/layout

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/link

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/list

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/loading-indicator

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/localized-input

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/management-toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/modal

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79663 Only render items if elements are provided ([20f3b19](https://github.com/liferay/liferay-portal/commit/20f3b193ef3a1cf1d5ee43a8a7bcaaee4bcd2dd2))
- LPD-79663 Add test ([d5d832d](https://github.com/liferay/liferay-portal/commit/d5d832d6c11e94cacbb78973c93443fe8e10906d))
- LPD-79663 Update snapshots ([b4906d5](https://github.com/liferay/liferay-portal/commit/b4906d5be205e68db99cb415e6da2ff850ea59d8))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/multi-select

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83372 Enable skipped clay-multi-select tests ([3764509](https://github.com/liferay/liferay-portal/commit/3764509a95729468bc1054782d6a9a406b25b665))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/multi-step-nav

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79381 Add as prop to Indicator to support custom tags ([626fe1a](https://github.com/liferay/liferay-portal/commit/626fe1acdc2106be3690f87c63820036b2a30511))
- LPD-79381 Add buttonProps and disabled support to MultiStepNav Indicator ([be6b4d5](https://github.com/liferay/liferay-portal/commit/be6b4d576a807c53d0dd85714b0ddbe4ce92cb86))
- LPD-79381 Add disabled props to the step Item ([cc280ca](https://github.com/liferay/liferay-portal/commit/cc280ca56bd641c07359db4be0e904cd4eff9e0b))
- LPD-79381 Add Disabled and NonInteractive stories to MultiStepNav ([d5db4bc](https://github.com/liferay/liferay-portal/commit/d5db4bc68041990beea9d0eac0a6633af135bb72))
- LPD-79381 Remove deprecated complete prop from MultiStepNav stories ([cbfed5f](https://github.com/liferay/liferay-portal/commit/cbfed5fd56bd3c52a688fd66fd4cf09c502ea1a7))
- LPD-79381 Respect 'as' prop and replace buttonProps with elementProps ([ffe7a0a](https://github.com/liferay/liferay-portal/commit/ffe7a0afabedbab5b236483bab2c30a0d5b2c58e))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/nav

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78996 @clayui/nav VerticalNav should allow stacked navs with no indent ([d824c08](https://github.com/liferay/liferay-portal/commit/d824c08a994b3babfd22902dcb739a9f0ed11e26))
- LPD-82605 Remove skip from tests, they pass ([228e368](https://github.com/liferay/liferay-portal/commit/228e3681671e3f3907f714b2f96b96dc15b9c0fd))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/navigation-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/pagination

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83387 Add global library ([b9fd33b](https://github.com/liferay/liferay-portal/commit/b9fd33b2f37a3f52c130ece8e208d1f7e0941b56))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/pagination-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/panel

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/popover

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-85584 Fix focus not returning to trigger after popover blur | When navigating via keyboard, focus moves to the popover when it opens. If a blur event occurs, focus is lost instead of returning to the previous element. This fix ensures that focus returns to the trigger element. ([29d35b0](https://github.com/liferay/liferay-portal/commit/29d35b01cf9bd77033f454e45d041493b1202b47))

## @clayui/progress-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79357 Add classname to progress bar ([cab0a14](https://github.com/liferay/liferay-portal/commit/cab0a14942e3ec5c95ece4f8671e51839297f65e))
- LPD-79357 @clayui/progress-bar adds fillBarClassName example in Storybook ([5130f70](https://github.com/liferay/liferay-portal/commit/5130f706f8c16440be3edece982c6f4f53e10455))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/provider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82604 Fix DataClient, which didn't provide a method to clear completed promises, breaking the CacheAndNetwork fetch policy ([b99f481](https://github.com/liferay/liferay-portal/commit/b99f481a35f2da518491229e6a78607bd6920213))
- LPD-66973 Retrigger focus ring animation on browser tab return ([886d229](https://github.com/liferay/liferay-portal/commit/886d2292438211b68c328c729542e23d1319bb67))
- LPD-66973 Avoid animation conflict and improve tab returning effect ([9f24b66](https://github.com/liferay/liferay-portal/commit/9f24b66a91eef63489c5ab44fd762ab3735f8bd1))
- LPD-66973 Use constants to avoid CSS class repetition ([565d0a2](https://github.com/liferay/liferay-portal/commit/565d0a26c78727edc74d538262fc66f8b07ebbc6))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/shared

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82606 Use fireEvent. userEvent was triggering keyups between ArrowRight events so the last part about acceleration was never working ([0c313b9](https://github.com/liferay/liferay-portal/commit/0c313b9533b46e30ab078777b03b5e45dc9b4b6b))
- LPD-84613 Move the usePanelWidthMax hook to clay-shared and rename it to useObservedMaxWidth ([6f6c2a7](https://github.com/liferay/liferay-portal/commit/6f6c2a771008635ed912dda53edf72240fdae6aa))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))
- LPD-69776 Rename useOverlayPositon to useOverlayPosition ([7152aae](https://github.com/liferay/liferay-portal/commit/7152aae19e8be4380d785f0c2bd7610923e3306c))
- LPD-69776 Reset sourceElement positioning ([435b602](https://github.com/liferay/liferay-portal/commit/435b602a0f282ec1b77267edd4eff65827d48214))
- LPD-69776 Add test coverage to doAlign positioning reset ([b137c44](https://github.com/liferay/liferay-portal/commit/b137c44f0976324e171159adcbad5aa7abe08851))
- LPD-88290 Move PanelResizer to clay-core ([ddde603](https://github.com/liferay/liferay-portal/commit/ddde603ae4e42d51ceb23cbbd8a117f42e0662cc))
- LPD-88290 Rename PanelResizer by ResizeHandle ([d2453ea](https://github.com/liferay/liferay-portal/commit/d2453ea7bf7321cdcf04b9681cca1ca2447074d0))

## @clayui/slider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/sticker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78752 Add product icon ([5f5fb3f](https://github.com/liferay/liferay-portal/commit/5f5fb3fc32d41276098bfd249fd0080f46ed062c))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/table

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/tabs

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83389 Remove tests related to modern and displayType props for ClayTabs since these attributes are not supported anymore and not used by the List component since Apr 10 2023 ([0cfc6a7](https://github.com/liferay/liferay-portal/commit/0cfc6a77f01c76e7e72fd42cc132bb23ea0c5c55))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/time-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/tooltip

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

## @clayui/upper-toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-86387 Update Clay to 3.161.0 ([191afb8](https://github.com/liferay/liferay-portal/commit/191afb830f09922e39bb9344a87ba276462f9a09))

# [3.161.0] (2026-04-16)

## @clayui/alert

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-81627 Allow passing classNames to the container ([1f9f4da](https://github.com/liferay/liferay-portal/commit/1f9f4da502edcfdd557a19645e383624cf9bb1fc))

## @clayui/autocomplete

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82600 Fix Escape key input ([e02bcac](https://github.com/liferay/liferay-portal/commit/e02bcac12cbca7dd8d379f2b6a1ab81928aab56b))

## @clayui/badge

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83029 Allow passing icon to badge ([26ceeb3](https://github.com/liferay/liferay-portal/commit/26ceeb3731afc917d0621aea45d967f16d2df0e5))
- LPD-83029 Add test ([ffa79b5](https://github.com/liferay/liferay-portal/commit/ffa79b5ed6336425bf6a5e9e06574a78ece42b58))
- LPD-83029 Implement ItemAfter, ItemBefore and ItemExpand logic ([19fcf1f](https://github.com/liferay/liferay-portal/commit/19fcf1f41c0cbb34cefb00bcd51f9cfcce7722b1))

## @clayui/breadcrumb

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/button

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/card

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))

## @clayui/color-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82601 Remove skip, test is working fine ([e0ad6fe](https://github.com/liferay/liferay-portal/commit/e0ad6fee201ce87c88db7b00453a265ee9c35785))
- LPD-80236 Convert index.ts into barrel file for exports ([ca8603a](https://github.com/liferay/liferay-portal/commit/ca8603a4049edfe93d16ad4b4dfa618362e35030))
- LPD-80236 Add useColorPicker hook ([82a441e](https://github.com/liferay/liferay-portal/commit/82a441e1e9a3969ae9e09d19e46607e304707916))
- LPD-80236 Add Field component to isolate the color input, splotch and dropdown ([74de4f2](https://github.com/liferay/liferay-portal/commit/74de4f237c92208742c97a8d25aae7157cb40375))
- LPD-80236 Export useColorPicker hook, Field and Editor components to reuse them ([2b55a42](https://github.com/liferay/liferay-portal/commit/2b55a42394e109edf4e454a4ea8e1cffd01cbe39))
- LPD-80236 Add the ability to add an external trigger reference ([13892d2](https://github.com/liferay/liferay-portal/commit/13892d25edd143ec7204c5c96b6870058ea9993a))
- LPD-80236 Adapt the parseColorValue function to the ClayColorPicker, taking into account the hex6 and hex8 ([c9d85e9](https://github.com/liferay/liferay-portal/commit/c9d85e96be51906c6c8699d95936255486f17418))
- LPD-80236 Save the value when onBlur is done in the Editor hex input ([ab85832](https://github.com/liferay/liferay-portal/commit/ab85832fee860e17e1a6df45b397987055a23059))
- LPD-80236 Improve props and descriptions ([6da7abb](https://github.com/liferay/liferay-portal/commit/6da7abb3a3dc9f6afcbf4eb24febbe592d4af6d9))
- LPD-80236 Rename onClickSplotch to onSplotchClick ([bec76ac](https://github.com/liferay/liferay-portal/commit/bec76acc04c3bbd9ed357adf790094fde7f09e29))
- LPD-80236 Create new function to format the hex colors ([bed726a](https://github.com/liferay/liferay-portal/commit/bed726a2fb3ce44c76a4157ac4adb2c93a77de40))
- LPD-83988 Export all the necessary functions from util.ts ([fdd7b2c](https://github.com/liferay/liferay-portal/commit/fdd7b2c82fcd839f34d5f5f7f29bd27b827c74c4))
- LPD-83988 Update the package types in the color-picker docs ([d2b0084](https://github.com/liferay/liferay-portal/commit/d2b00844c744b7175051336914471d4dd3d19957))

## @clayui/core

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78752 Allow configuring custom classes in Clay SidePanel.Title ([5626239](https://github.com/liferay/liferay-portal/commit/56262393602378d933b9d7962fc609700b4e8407))
- LPD-79027 Use existing onEnd callback ([d6d039d](https://github.com/liferay/liferay-portal/commit/d6d039d7073cbc6558ca4ab14bc08eb372c035bb))
- LPD-79027 Reset state also after drop with mouse ([03de461](https://github.com/liferay/liferay-portal/commit/03de46132d6ac765df42adc6452da3ab3280a493))
- LPD-78996 @clayui/nav VerticalNav should allow stacked navs with no indent ([d824c08](https://github.com/liferay/liferay-portal/commit/d824c08a994b3babfd22902dcb739a9f0ed11e26))
- LPD-78996 @clayui/core VerticalNav add example without indent ([60adeec](https://github.com/liferay/liferay-portal/commit/60adeec5ac228a6ae1e7a39042c3770a6c0c9a9d))
- LPD-79543 Allow side panel to disable the escape key behavior ([1aa3734](https://github.com/liferay/liferay-portal/commit/1aa3734ee319c84899b114eef93587bdf0fd9658))
- LPD-66630 Update size to small ([096b025](https://github.com/liferay/liferay-portal/commit/096b02530c74447b42f14aa7383aeef733559b6f))
- LPD-66630 Update snapshots ([4f5e77b](https://github.com/liferay/liferay-portal/commit/4f5e77b42757bc9a0d4df11a6a6826156bfaa976))
- LPD-79219 @clayui/core Treeview drag handler should be 24px x 24px when focused ([2cc084a](https://github.com/liferay/liferay-portal/commit/2cc084ae2a760b05df962eff3aff1e9b920383ba))
- LPD-75776 Fix typo ([9d38ea9](https://github.com/liferay/liferay-portal/commit/9d38ea9f9a3745e95d519a66ece726ad13fcf40d))
- LPD-81627 Make symbol optional ([2343e95](https://github.com/liferay/liferay-portal/commit/2343e95f6dc7d6d4f0d3fd6b2d4b1220b2002291))
- LPD-82602 Fix keyboard events ([cde7716](https://github.com/liferay/liferay-portal/commit/cde7716b174daa483aa7724e28846ccc94eb246c))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-82603 Fix escape key input in skipped side panel test ([e0029ee](https://github.com/liferay/liferay-portal/commit/e0029eec3a6084e0a952a4f4e5f7c1fb66e59e21))
- LPD-80239 Add FocusTrap to the dropdown menu ([0a906b5](https://github.com/liferay/liferay-portal/commit/0a906b542b5475edf2e05f4ed04eaf61dd23967b))
- LPD-85711 Add opt-in search capability to the Picker component ([f14c1f9](https://github.com/liferay/liferay-portal/commit/f14c1f94459d7a0876c7a5dfa51bf8df6b328daf))
- LPD-85711 Add test coverage to Picker search capability ([8201b1e](https://github.com/liferay/liferay-portal/commit/8201b1e67ea5acfd5ce9d292a022ec518b041d79))
- LPD-85711 Update Picker documentation to include the new search capability ([d465000](https://github.com/liferay/liferay-portal/commit/d4650005e444fb892fd2af4dfa1bab4ae9634e73))
- LPD-85711 Refactor Picker component to modularize search logic ([4c7178e](https://github.com/liferay/liferay-portal/commit/4c7178e991a4abe6dd5b843f3f80f23bf6ec8ef3))
- LPD-85711 Improve search accessibility ([a8a06e0](https://github.com/liferay/liferay-portal/commit/a8a06e0f6a5a61196a6dec4ead16716f5d423720))
- LPD-85711 Reorganize props ([1fca9cd](https://github.com/liferay/liferay-portal/commit/1fca9cd03e891910f79f92a26491d3f2ce42c242))

## @clayui/css

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-75997 @clayui/css Update product-menu-open.svg ([1db5459](https://github.com/liferay/liferay-portal/commit/1db545935c65cc6ca37ae37b57f27951d42405f6))
- LPD-79073 Control Menu button icons should be 16px ([36a0f8a](https://github.com/liferay/liferay-portal/commit/36a0f8adaba142c08d1ac8c460ed919ee81ff9c9))
- LPD-77928 @clayui/css SF variable files ([1fe4e68](https://github.com/liferay/liferay-portal/commit/1fe4e6817515190e3f41d435bc62f3436ba59c0d))
- LPD-79018 @clayui/css sticker-xxl icon should be 40px ([2fd0409](https://github.com/liferay/liferay-portal/commit/2fd0409656bdda0f9e2f765891de2a45660e4237))
- LPD-80362 @clayui/core Treeview icon angle-down should display larger ([22eb0b3](https://github.com/liferay/liferay-portal/commit/22eb0b3c7b87daa3ee9d61538dad07303d844537))
- LPD-78750 @clayui/css adds rocket.svg ([340f130](https://github.com/liferay/liferay-portal/commit/340f13097d6f9f3884532d483edaf83b5c6dd3f3))
- LPD-79219 @clayui/core Treeview drag handler should be 24px x 24px when focused ([2cc084a](https://github.com/liferay/liferay-portal/commit/2cc084ae2a760b05df962eff3aff1e9b920383ba))
- LPD-80374 @clayui/css add icons calculator, price-list, product-configuration, and products ([cffbeff](https://github.com/liferay/liferay-portal/commit/cffbeff59e3509c2b132969f49d4d5ce1898c232))
- LPD-80374 SF icons.svg ([c27b09c](https://github.com/liferay/liferay-portal/commit/c27b09c03777ee017fe66d7ebd5a93e66ff4e3fc))
- LPD-77810 @clayui/css Replace Sass math with CSS calc ([75730ce](https://github.com/liferay/liferay-portal/commit/75730ce27d3621b0c73c5256be4568891d44eec8))
- LPD-77810 @clayui/css Replace math-sign with CSS calc ([42a8656](https://github.com/liferay/liferay-portal/commit/42a865608c05a36c2d2fd5b313b733a8d0709726))
- LPD-77810 @clayui/css SF ([4eb1545](https://github.com/liferay/liferay-portal/commit/4eb154584aeb8bdbd82038223b5a719b81769282))
- LPD-80439 @clayui/css add layout-new-window.svg ([2310a7f](https://github.com/liferay/liferay-portal/commit/2310a7f5800515f39300f77362ccf8415d202c0f))
- LPD-79381 Scope cursor and hover effects to interactive elements ([8ae76f2](https://github.com/liferay/liferay-portal/commit/8ae76f2912116397bdad8bade5e7c8e6faa7c26c))
- LPD-81799 - Regen ([e577af8](https://github.com/liferay/liferay-portal/commit/e577af8942abc4abfc9811ba91781239cf08ef6d))
- LPD-81799 - SF ([3a6f4e5](https://github.com/liferay/liferay-portal/commit/3a6f4e5c95e8e68de19e2902eb8b673311a241c2))
- LPD-80549 @clayui/css clay-css mixin should support any CSS property ([49fc8b8](https://github.com/liferay/liferay-portal/commit/49fc8b8ad1a059fad8facc1f5e164c1373466270))
- LPD-81799 clayui.com should use local version of icons.svg in dev mode ([d57b27c](https://github.com/liferay/liferay-portal/commit/d57b27cec0cf0098114a233eced849776d0d9cd5))
- LPD-81820 @clayui/css add unit to 0 values to make it work with calc() ([9a5ffbf](https://github.com/liferay/liferay-portal/commit/9a5ffbffbb75908bc23dae70b177bcf434507fab))
- LPD-82273 @clayui/css remove use of Sass nth function with variables that could contain a CSS variable ([503c60e](https://github.com/liferay/liferay-portal/commit/503c60e225c5a2b223c7476fc007325d86172433))
- LPD-77641 @clayui/css $custom-control-indicator-checked-color should propagate to checkbox and radio ([02fe938](https://github.com/liferay/liferay-portal/commit/02fe938e328af2c1a472d06ae8ab20d55d018242))
- LPD-83068 @clayui/css add books-brush.svg ([3d3f1b2](https://github.com/liferay/liferay-portal/commit/3d3f1b221cf72464d7f28ec9ea0c907c8775cf31))
- LPD-71471 @clayui/css clay-data-label-text-position function shouldn't error with CSS variables ([a88e120](https://github.com/liferay/liferay-portal/commit/a88e1203878e72258f746b31633cfd03b3666090))
- LPD-71471 @clayui/css Use calc for $spacers ([3a8d947](https://github.com/liferay/liferay-portal/commit/3a8d9476d2c67482bc535ebf16aad92fa46f9f7a))
- LPD-71471 @clayui/css Add Atlas custom properties theme ([868a5cf](https://github.com/liferay/liferay-portal/commit/868a5cf310d35d0ea06ade8e0882057710af3028))
- LPD-81854 @clayui/css atlas variable import order should match base variable import order ([1c52c57](https://github.com/liferay/liferay-portal/commit/1c52c57b0f9427edea79b6116ec9dc2d0a2a87df))

## @clayui/data-provider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82604 Fix clay-data-provider test with CacheFirst, it was expecting the cache to be already populated ([38fb06d](https://github.com/liferay/liferay-portal/commit/38fb06d2913f0b985ae53010ff60de76601ad0c8))
- LPD-82604 Fix DataClient, which didn't provide a method to clear completed promises, breaking the CacheAndNetwork fetch policy ([b99f481](https://github.com/liferay/liferay-portal/commit/b99f481a35f2da518491229e6a78607bd6920213))
- LPD-82604 Fix clay-data-provider test with CacheAndNetwork, it was expecting the cache to be already populated ([337d076](https://github.com/liferay/liferay-portal/commit/337d07695378423d84cd4aace237df41cbf49cca))
- LPD-82604 Fix clay-data-provider test using Node.js-specific unref method, adapting it to reflect the browser environment ([a540d95](https://github.com/liferay/liferay-portal/commit/a540d95ebaffac1d8b31b5d53741ab2e88a8add4))

## @clayui/date-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/drop-down

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))
- LPD-82700 Remove aria-labelledby for DropDown groups without header ([dd8f061](https://github.com/liferay/liferay-portal/commit/dd8f061b49d3a731d14a2e101e6abed533ff135b))
- LPD-83370 Enable DropDownWithDrilldown skipped tests ([aefacb2](https://github.com/liferay/liferay-portal/commit/aefacb276312ec759f3554e41b37f04932b1f067))

## @clayui/empty-state

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/form

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-80237 Allow passing refs to the Clay Select ([55e2649](https://github.com/liferay/liferay-portal/commit/55e2649bdc0aef25991539f1d4fa9c4e4a212645))
- LPD-83371 Unskip test. Use the callback function to test the multiple options ([c13571d](https://github.com/liferay/liferay-portal/commit/c13571d350579998edfa8aa6228f778f8d81e74a))

## @clayui/icon

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78750 @clayui.com add rocket.svg to docs ([e74d242](https://github.com/liferay/liferay-portal/commit/e74d2429a8f8c1b6fc1e328bd68ff82322f8fc22))
- LPD-80374 clayui.com add aliases for calculator, price-list, product-configuration and products icons ([c47704a](https://github.com/liferay/liferay-portal/commit/c47704a0a6032017996175da941ab6062fed9ff9))
- LPD-80439 clayui.com document layout-new-window.svg ([7422fbf](https://github.com/liferay/liferay-portal/commit/7422fbfd99740a28d6c088cddec5dcaf3fa77652))
- LPD-83068 clayui.com add books-brush.svg and aliases ([5e950e1](https://github.com/liferay/liferay-portal/commit/5e950e19fe9fa530ee36a2813a6d4ce8f06b31ef))

## @clayui/label

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/layout

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/link

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/list

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/loading-indicator

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/localized-input

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/management-toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/modal

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79663 Only render items if elements are provided ([20f3b19](https://github.com/liferay/liferay-portal/commit/20f3b193ef3a1cf1d5ee43a8a7bcaaee4bcd2dd2))
- LPD-79663 Add test ([d5d832d](https://github.com/liferay/liferay-portal/commit/d5d832d6c11e94cacbb78973c93443fe8e10906d))
- LPD-79663 Update snapshots ([b4906d5](https://github.com/liferay/liferay-portal/commit/b4906d5be205e68db99cb415e6da2ff850ea59d8))

## @clayui/multi-select

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83372 Enable skipped clay-multi-select tests ([3764509](https://github.com/liferay/liferay-portal/commit/3764509a95729468bc1054782d6a9a406b25b665))

## @clayui/multi-step-nav

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79381 Add as prop to Indicator to support custom tags ([626fe1a](https://github.com/liferay/liferay-portal/commit/626fe1acdc2106be3690f87c63820036b2a30511))
- LPD-79381 Add buttonProps and disabled support to MultiStepNav Indicator ([be6b4d5](https://github.com/liferay/liferay-portal/commit/be6b4d576a807c53d0dd85714b0ddbe4ce92cb86))
- LPD-79381 Add disabled props to the step Item ([cc280ca](https://github.com/liferay/liferay-portal/commit/cc280ca56bd641c07359db4be0e904cd4eff9e0b))
- LPD-79381 Add Disabled and NonInteractive stories to MultiStepNav ([d5db4bc](https://github.com/liferay/liferay-portal/commit/d5db4bc68041990beea9d0eac0a6633af135bb72))
- LPD-79381 Remove deprecated complete prop from MultiStepNav stories ([cbfed5f](https://github.com/liferay/liferay-portal/commit/cbfed5fd56bd3c52a688fd66fd4cf09c502ea1a7))
- LPD-79381 Respect 'as' prop and replace buttonProps with elementProps ([ffe7a0a](https://github.com/liferay/liferay-portal/commit/ffe7a0afabedbab5b236483bab2c30a0d5b2c58e))

## @clayui/nav

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78996 @clayui/nav VerticalNav should allow stacked navs with no indent ([d824c08](https://github.com/liferay/liferay-portal/commit/d824c08a994b3babfd22902dcb739a9f0ed11e26))
- LPD-82605 Remove skip from tests, they pass ([228e368](https://github.com/liferay/liferay-portal/commit/228e3681671e3f3907f714b2f96b96dc15b9c0fd))

## @clayui/navigation-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/pagination

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83387 Add global library ([b9fd33b](https://github.com/liferay/liferay-portal/commit/b9fd33b2f37a3f52c130ece8e208d1f7e0941b56))

## @clayui/pagination-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/panel

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/popover

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/progress-bar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-79357 Add classname to progress bar ([cab0a14](https://github.com/liferay/liferay-portal/commit/cab0a14942e3ec5c95ece4f8671e51839297f65e))
- LPD-79357 @clayui/progress-bar adds fillBarClassName example in Storybook ([5130f70](https://github.com/liferay/liferay-portal/commit/5130f706f8c16440be3edece982c6f4f53e10455))

## @clayui/provider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82604 Fix DataClient, which didn't provide a method to clear completed promises, breaking the CacheAndNetwork fetch policy ([b99f481](https://github.com/liferay/liferay-portal/commit/b99f481a35f2da518491229e6a78607bd6920213))

## @clayui/shared

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82606 Use fireEvent. userEvent was triggering keyups between ArrowRight events so the last part about acceleration was never working ([0c313b9](https://github.com/liferay/liferay-portal/commit/0c313b9533b46e30ab078777b03b5e45dc9b4b6b))

## @clayui/slider

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/sticker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-78752 Add product icon ([5f5fb3f](https://github.com/liferay/liferay-portal/commit/5f5fb3fc32d41276098bfd249fd0080f46ed062c))

## @clayui/table

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/tabs

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-83389 Remove tests related to modern and displayType props for ClayTabs since these attributes are not supported anymore and not used by the List component since Apr 10 2023 ([0cfc6a7](https://github.com/liferay/liferay-portal/commit/0cfc6a77f01c76e7e72fd42cc132bb23ea0c5c55))

## @clayui/time-picker

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/tooltip

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))

## @clayui/upper-toolbar

### Commits

- LPD-78372 - Run SF ([763733b](https://github.com/liferay/liferay-portal/commit/763733b6e5142030b6cd233c6cff7562d0763bf5))
- LPD-82700 Conditionally add aria-controls attribute to ClayDropDown trigger, so it won't reference a element that isn't present in the DOM ([0b476ba](https://github.com/liferay/liferay-portal/commit/0b476bac00fa0482cc266f2f7ff4628213333350))