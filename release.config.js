module.exports = {
    branches: ['main'],
    preset: "conventionalcommits",
    plugins: [
        '@semantic-release/commit-analyzer',
        '@semantic-release/release-notes-generator',
        '@semantic-release/changelog',
        [
          "@semantic-release/github",
          {
            "assets": [
              {
                "path": "target/scala-*/SCALcetto.jar",
                "label": "SCALcetto jar"
              }
            ]
          }
        ],
        '@semantic-release/git',
        '@semantic-release/github'
    ]
}