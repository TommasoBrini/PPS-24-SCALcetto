## [3.1.0](https://github.com/TommasoBrini/PPS-24-SCALcetto/compare/v3.0.0...v3.1.0) (2025-07-11)

### Features

* Move validate logic in DecisionValidator, move act logic in ActionProcessor ([d4276d1](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/d4276d1d2e99b4dc6e45f9459335ce3e8d448d67))
* **release:** new release ([a72bd9e](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/a72bd9e0842597930ff12f211528c987947e2095))
* remove solo syntax ([323e585](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/323e5854990aa9e0ddb0703a7feb8cad64ad9912))
* score initial structure injected ([68fba64](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/68fba64353522bcead51b878a1db4d98addb8089))
* score works correctly ([bde338e](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/bde338e69dbf10a628eee0ee45eddcf5cc0bd0db))

### Bug Fixes

* fix bug in move to goal direction ([295ed94](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/295ed94d1f76b57cf7ae5a1dd7143e3005cdc848))

## [3.0.0](https://github.com/TommasoBrini/PPS-24-SCALcetto/compare/v2.0.0...v3.0.0) (2025-07-08)

### ⚠ BREAKING CHANGES

* **release:** new release

### Features

* create util functions ([45e185d](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/45e185de77e3aae4ad26b02c2df916f735848ab7))
* implement correct rating for control player behavior ([573ca2f](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/573ca2fa8a023bda5abc99a147fc7bb37ca4946c))
* implement opponent behavior ([5125ff6](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/5125ff65bd81b491a26528bde38eeea5d776dac7))
* implements mixin for decide, change decide architecture ([3080d7b](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/3080d7b9df050b951fe8e2dffdc4d36ddb17f6e4))
* init new builder ([8890f9a](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/8890f9ae1ba6a9d3808b4df2423d74b67735e836))
* Refactor act, make update flow more functional ([6c865bf](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/6c865bfc48d367cc961ae1e7c04082fd9c7d26ae))
* **release:** new release ([76f739c](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/76f739ced95b2ecc780260f3c64544ab91d2f06a))
* resolve movement out of field ([f998327](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/f99832709cb05928ffb7f3fd0930f54bc4462b84))
* working dsl creation and structure ([3b94b9c](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/3b94b9c90bffe73543f7f154ce69eec2b982ed55))
* working structure for creational DSLs ([4e85a52](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/4e85a5221bf8fe996251bd50e0751cbd31f399dd))

### Bug Fixes

* fix bug in decision pass ([94f495a](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/94f495a3ed975ffb04ecf74506862f071fd10ca3))
* player in the middle now spawns correctly ([42a1c83](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/42a1c834f7d9a6df9bffeab672ca023feefc19c3))
* refactor tests ([0f610bd](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/0f610bdd6c3baa62a9135f5646bf29de85637214))

## [2.0.0](https://github.com/TommasoBrini/PPS-24-SCALcetto/compare/v1.0.0...v2.0.0) (2025-06-27)

### ⚠ BREAKING CHANGES

* **release:** new release

### Features

* Add direction jitter when a pass is failed ([0e843b6](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/0e843b6820ad6a500120318933ffb1bdd0e441d5))
* add goal feature tdd ([dad49a2](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/dad49a222991a905371f454c763a3a09a09374e2))
* add logic and maths for shooting ([811d589](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/811d5897baf2c908c440a2a9b95e30869522c459))
* Add more specific actions for player, add possible passes and pass rating computation ([a8b1d2a](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/a8b1d2a69ad2fb1c86070a29ab4b8300128648ce))
* add shoot accuracy ([f5dc7fc](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/f5dc7fcfccc327e6055efcef0745ea9ef00ca1dc))
* Add teammate movement towards the ball and ball reception ([020cd74](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/020cd74e65cab624571e960d25c6c510875549b3))
* add valite layer between decide and act ([676e268](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/676e26866461d6dce6ff11cd1fb863840308baf2))
* implement ball possession in team ([f943ddc](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/f943ddca531bd81a4cb45183ee109f0a5e008731))
* implement correct behaviour for opponent players ([9e1b546](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/9e1b5467c91839e6ea608fb83f11edb2cabc9b94))
* implement move control player ([a7eb57b](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/a7eb57be43d00f54d788c69e885214a64e41b06c))
* implement stopped action ([3f04def](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/3f04def7f282b7d14d91048db38a56c0edbe8899))
* implement tackle and intercept ([8c43b79](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/8c43b79bb712e2574862f49dd3a4bb2c6dd6e369))
* **release:** new release ([e46706c](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/e46706c8708c8d362c7b720a1b46993a2f350c63))
* shoot works ([2b643be](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/2b643be0dac2cd556478b0a8deb95ad181f9b3a7))

### Bug Fixes

* Add corner case for returning Direction.none when both distances are zero in getDirection ([c465a15](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/c465a15366b91842e3bd943447d981992189feae))
* fix bug in isGoal ([db18f1e](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/db18f1ed2eba4a340532c171d7f0c6629a0ee57b))

## [1.0.0](https://github.com/TommasoBrini/PPS-24-SCALcetto/compare/v0.2.0...v1.0.0) (2025-06-17)

### ⚠ BREAKING CHANGES

* **release:** first release

### Features

* **release:** first release ([38a42d3](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/38a42d3ef8df9190544f85817beb70597d2299f3))

# [0.2.0](https://github.com/TommasoBrini/PPS-24-SCALcetto/compare/v0.1.1...v0.2.0) (2025-06-17)


### Bug Fixes

* add test file to scala tests ([160a0ac](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/160a0acdef5bd70b32402933183412a3645f86e5))
* Make Position have its method to generate a Direction to another Position ([3f4a0fc](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/3f4a0fccf955f45a07b518db5c3f5216711c01f6))
* Merge develop with feature/act_phase ([a0eec31](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/a0eec31cfeaa22559ba1b64e645db6e5fe220262))


### Features

* Add act phase for updating entities movements and move them ([e1e6bd8](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/e1e6bd85b6ae4ff32a43736fab064e0e092331c9))
* change gui with fps mechanism ([f4a2020](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/f4a202062ffc9b776d271a8858615772fab2c979))
* **gui:** implement new simple version of view ([4604cd2](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/4604cd23e3d4212bdf1122e5144ad5095e9deae1))
* Implement ball out of field event and ball bounce ([130fd1f](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/130fd1ff7d9755ea1e21474a213b14768fdc4173))
* implement decide for player in control team ([b411da1](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/b411da1f364a046d408eb68c67cbdb5d1a1820c5))
* implement decide for player with no ball control ([c2f6ebf](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/c2f6ebf3f70b971a8a07fef93385f291ac42ec62))
* Implement gain ball control for player in range of it ([3446324](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/34463249028877eec2cf2b27ef7e67ff84b5f57f))
* implement model factory and refactor gui ([dcbd27e](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/dcbd27ea483ccbc4d383a0c69d7de8183a0badcb))
* implement simple gui ([0399e13](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/0399e134d349e8a3a6b8a5ed84c35e1b4483ebd6))
* implement start, stop and resume. Refactor of game loop ([15892c6](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/15892c6935f4ca8c3a483116ed266873d9b89712))
* init architecture boilerplate ([155b188](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/155b18829ecece0eb3e369b41d10d40efb80ab0a))

## [0.1.1](https://github.com/TommasoBrini/PPS-24-SCALcetto/compare/v0.1.0...v0.1.1) (2025-05-22)


### Bug Fixes

* fix bugs in CI ([f886b15](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/f886b15db67ea40e15213ed4f73e711527284e15))
* fix bugs in CI ([e0f4c3a](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/e0f4c3aaff962c343f20a0ff4b99a3a0e9043586))

# [0.1.0](https://github.com/TommasoBrini/PPS-24-SCALcetto/compare/v0.0.0...v0.1.0) (2025-05-22)


### Bug Fixes

* fix bug in CI ([936a788](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/936a788099273bfb4252708278a0fe0ac0e75c2c))
* fix bug in CI ([051eb35](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/051eb3503e999e6cf134e48500048381e5fcedb3))


### Features

* **core:** initialize CI and setup project ([9956802](https://github.com/TommasoBrini/PPS-24-SCALcetto/commit/99568027159732a330afe6263af2ca0e6eaebc05))
