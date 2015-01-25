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
* compile an initial pipeline application with `lein ring uberjar`, move the resulting jar to the root directory, call it `pipeline-0-standalone.jar` (ansible will look for this file to set up the initial ci server)
* in `resources`: 
  * spin up a cloudformation-stack using `infrastructure-template.json`
  * put the new servers ip into the `inventory`
  * run the ansible `playbook.yml`
* you now have a fully functional continuous delivery pipeline listening on port 8080 of your new server. click the manual trigger to deploy a demo application
* your pipeline code is in `src/`. Make some changes, push them and have a look at your meta-pipeline. it will now build a new instance of the pipeline and (after you trigger the last step) redeploy itself. 


## License

Copyright © 2014 Florian Sellmayr

Distributed under the Apache License 2.0
