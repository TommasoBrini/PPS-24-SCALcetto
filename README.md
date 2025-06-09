
## BUILD STATUS
![Build](https://github.com/TommasoBrini/PPS-24-SCALcetto/actions/workflows/Test_Scala.yml/badge.svg)
![Release](https://github.com/TommasoBrini/PPS-24-SCALcetto/actions/workflows/Release.yml/badge.svg)

## COVERAGE
MAIN ![Coverage](https://codecov.io/gh/TommasoBrini/PPS-24-SCALcetto/branch/main/graph/badge.svg)

DEVELOP ![Coverage](https://codecov.io/gh/TommasoBrini/PPS-24-SCALcetto/branch/develop/graph/badge.svg)

## INFO
![Version](https://img.shields.io/github/v/release/TommasoBrini/PPS-24-SCALcetto?include_prereleases)
![License](https://img.shields.io/github/license/TommasoBrini/PPS-24-SCALcetto)

# CONTRIBUTE
Once you clone this repo, run in your shell or in the intellij sbt shell
``` bash
sbt setupHooks
```
# BUILD
To build and run the project use sbt
``` bash
sbt compile
sbt run
```
to format automatically the code:
``` bash
sbt scalafmtAll
```
# TEST
if you want run all the tests
``` bash
sbt test
```

# Docs
You can see the live doc pushed in develop directly in the link in the about section. 

You can run locally the docs:
- install jekyll => if you have linux you should have wsl installed
- run the following commands in the shell in the `docs/` directory
``` bash
bundle install
bundle exec jekyll serve
```