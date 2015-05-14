# devops-101-lambdacd

Sample code for a continuous delivery infrastructure in pure code: 

* Spin up a [LambdaCD](https://github.com/flosell/lambdacd)-Instance on DigitalOcean
* Use LambdaCD to compile, test, and deploy an dummy application on DigitalOcean
* inspired by [devops-101](https://github.com/kgxsz/devops-101)

## Requirements

* A [DigitalOcean](https://www.digitalocean.com/) account

* Ansible
* Tugboat
* Leiningen
* Bash

## Usage

_(Note: this could be fully automated but for demonstration purposes currently isn't)_

* fork and clone this repo
* configure your own fork as the repo in `src/devops_101_pipeline/meta`, commit and push your changes
* call `bin/setup-env/set-up-env.sh` to set up an initial environment with two servers, one for you app, the other one for lambdacd
* call `export LAMBDACD_HOST=<ci server ip>`
* call `lein run` to start LambdaCD
* open LambdaCD in your browser: http://localhost:8080
* run the meta-pipeline, this will deploy LambdaCD from the repository to the server you specified in `LAMBDACD_HOST`
* after the meta-pipeline is done, you now have a fully functional LambdaCD instance on your server. You can stop the local instance.
  You can now run your pipeline to deploy a dummy app to the server.
  Make changes to your pipeline and push them. This will trigger the meta-pipeline, deploying a new version of LambdaCD with your changes.



## License

Copyright © 2014 Florian Sellmayr

Distributed under the Apache License 2.0
