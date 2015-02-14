# devops-101-lambdacd

Sample code for a continuous delivery infrastructure in pure code: 

* Spin up a [LambdaCD](https://github.com/flosell/lambdacd)-Instance on AWS
* Use LambdaCD to compile, test, and deploy an dummy application on AWS
* based on [devops-101](https://github.com/kgxsz/devops-101)

## Requirements

* Ansible
* AWS CLI
* Leiningen

## Usage

* fork and clone this repo
* configure your own fork as the repo in `src/devops_101_pipeline/meta`
* `bin/set-up-everything.sh` will set up an initial pipeline on AWS that will deploy a dummy application.
* your pipeline code is in `src/`. Make some changes, push them and have a look at your meta-pipeline. It will now build a new instance of the pipeline and (after you trigger the last step) redeploy itself.


## License

Copyright © 2014 Florian Sellmayr

Distributed under the Apache License 2.0
