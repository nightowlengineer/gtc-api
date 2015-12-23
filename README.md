# GTC API

This API provides a series of REST resources that allow various users to manage various items - for now this is centering around members, and in the future books.

## System Requirements
### Hardware
- Storage: 5GB
- Memory: 1GB

### Software
- Java Runtime >= 1.8
- MongoDB >= 3.2

### Misc
- Network connectivity
- Valid SSL certificate

## Development

You will need an IDE such as Eclipse, an account for GitLab, access to 'TheGTC' group and projects, Git, Maven, as well as the runtime software requirements for the application.

#### Branching strategy

The current strategy is to develop on `master`. When the first release starts to be used day-to-day, a `develop` branch will be introduced which will instead be used for mainline development. At the point of release, this branch will be merged into the `master` branch.

Branches can be created off the branch being developed against, and merged in as appropriate. Merge requests and code reviews will be required if additional developers work on this project.

#### Handy commands

Compile and run unit tests:
```sh
$ mvn clean test
```

Compile and package:
```sh
$ mvn clean package
```

Run application:
```
java -jar path/to/packaged.jar server config.yml
```