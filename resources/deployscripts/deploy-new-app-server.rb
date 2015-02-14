#!/usr/bin/ruby

require 'rubygems'
require 'json'

def extract_subnet_id
  describe_subnets_command = "aws ec2 describe-subnets \
                              --filters Name=tag:aws:cloudformation:logical-id,Values=Subnet \
                              --region eu-west-1 \
                              --output json"
  subnets = JSON.parse(`#{describe_subnets_command}`)["Subnets"]
  subnet_id = subnets.first["SubnetId"]
  puts "Extracted subnet id: #{subnet_id}"
  return subnet_id
end

def extract_security_group_id
  describe_security_groups_command = "aws ec2 describe-security-groups \
                                      --filters Name=tag:aws:cloudformation:logical-id,Values=SecurityGroup \
                                      --region eu-west-1 \
                                      --output json"
  security_groups = JSON.parse(`#{describe_security_groups_command}`)["SecurityGroups"]
  security_group_id = security_groups.first["GroupId"]
  puts "Extracted security group id: #{security_group_id}"
  return security_group_id
end

def commence_creation_of_stack(s3_uri, subnet_id, security_group_id,build_id)
  puts "Commencing creation of stack: app-server-#{build_id}"
  `aws cloudformation create-stack \
  --stack-name app-server-#{build_id} \
  --template-body "file://./app-server-template.json" \
  --region eu-west-1 \
  --output text \
  --parameters \
  ParameterKey=SubnetId,ParameterValue=#{subnet_id} \
  ParameterKey=SecurityGroupId,ParameterValue=#{security_group_id} \
  ParameterKey=ArtifactUri,ParameterValue=#{s3_uri} \
  --capabilities CAPABILITY_IAM`
end

def check_that_the_stack_has_been_created(build_id)
  describe_stacks_command = "aws cloudformation describe-stacks \
                             --stack-name app-server-#{build_id} \
                             --region eu-west-1 \
                             --output json"
  stacks = JSON.parse(`#{describe_stacks_command}`)["Stacks"]
  stack_status = stacks.first["StackStatus"]

  puts "Awaiting creation of app-server-#{build_id} with status of #{stack_status}"

  if stack_status == "CREATE_COMPLETE"
    stackip=stacks.first["Outputs"].first["OutputValue"]
    puts "Stack creation complete, application now available at http://#{stackip}:8080/"
    exit 0
  elsif stack_status != "CREATE_IN_PROGRESS"
    puts "Stack creation failed"
    exit 1
  end
end

def main(s3_uri,build_id)
  subnet_id = extract_subnet_id
  security_group_id = extract_security_group_id
  commence_creation_of_stack(s3_uri, subnet_id, security_group_id,build_id)
  loop do
    check_that_the_stack_has_been_created(build_id)
    sleep(15)
  end
end

main(ARGV[0],ARGV[1])
