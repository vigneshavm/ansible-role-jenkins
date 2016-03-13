#!/usr/bin/python


from ansible.module_utils.basic import *  # NOQA
import json
from os.path import basename


def main():

    module = AnsibleModule(
        argument_spec=dict(
            name=dict(
                type='str',
                required=True),
            server_url=dict(
                type='str',
                required=True),
            container_cap=dict(
                type='int',
                required=True),
            connect_timeout=dict(
                type='int',
                required=True),
            read_timeout=dict(
                type='int',
                required=True),
            credentials_id=dict(
                type='str',
                required=False),
            templates=dict(
                type='list',
                required=True),
            state=dict(
                type='str',
                required=False,
                default='present',
                choice=['present', 'absent']),
            cli_path=dict(
                type='str',
                required=False,
                default='/var/lib/jenkins/jenkins-cli.jar'),
            groovy_scripts_path=dict(
                type='str',
                required=False,
                default='/var/lib/jenkins/groovy_scripts'),
            url=dict(
                type='str',
                required=False,
                default='http://localhost:8080')
        )
    )

    script = "%s/%s.groovy" % (module.params['groovy_scripts_path'],
                               basename(__file__))

    rc, stdout, stderr = module.run_command(
        "java -jar %s -s '%s' groovy %s '%s'" %
        (module.params['cli_path'], module.params['url'], script,
         json.dumps(module.params)))

    if (rc != 0):
        module.fail_json(msg=stderr)

    json_stdout = json.loads(stdout)
    module.exit_json(changed=bool(json_stdout['changed']),
                     output=json_stdout['output'])


if __name__ == '__main__':
    main()