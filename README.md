# SCALcetto
SCALcetto is a football match simulator developed in **Scala 3** that implements an intelligent player system.

![Video Demo](assets/Simulation2.gif)
<br/>

<div style="display: flex; justify-content: space-around; align-items: center; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9;">

  <a href="https://tommasobrini.github.io/PPS-24-SCALcetto/" target="_blank" style="text-decoration: none; color: #0366d6; font-weight: bold;">
    <div style="text-align: center; padding: 10px;">
      <img src="https://img.shields.io/badge/Docs-Online-blue?style=for-the-badge" alt="Online Docs">
      <p>View Documentation</p>
    </div>
  </a>

  <a href="https://github.com/TommasoBrini/PPS-24-SCALcetto/releases/download/v3.2.0/SCALcetto.jar" download style="text-decoration: none; color: #0366d6; font-weight: bold;">
    <div style="text-align: center; padding: 10px;">
      <img src="https://img.shields.io/badge/Download-JAR-green?style=for-the-badge" alt="Download JAR">
      <p>Download JAR File</p>
    </div>
  </a>

</div>

## BUILD STATUS
![Build](https://github.com/TommasoBrini/PPS-24-SCALcetto/actions/workflows/Test_Scala.yml/badge.svg)
![Release](https://github.com/TommasoBrini/PPS-24-SCALcetto/actions/workflows/Release.yml/badge.svg)

## COVERAGE
MAIN [![codecov](https://codecov.io/gh/TommasoBrini/PPS-24-SCALcetto/branch/main/graph/badge.svg?token=8MS683R0Q7)](https://codecov.io/gh/TommasoBrini/PPS-24-SCALcetto)

DEVELOP [![codecov](https://codecov.io/gh/TommasoBrini/PPS-24-SCALcetto/branch/develop/graph/badge.svg?token=8MS683R0Q7)](https://codecov.io/gh/TommasoBrini/PPS-24-SCALcetto)

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