import { realpathSync, existsSync, writeFileSync } from 'fs';
import dotenv_expand from 'dotenv-expand';
import { config } from 'dotenv';

const env = getEnvironment();

function getEnvironment() {
    // const dotenvFile = realpathSync("/secret/.env");
    const dotenvFile = '/secret/.env'
    if (existsSync(dotenvFile)) {
        dotenv_expand(config({ path: dotenvFile, }));
    }

    const prefix = "REACT_APP";
    const envList = Object.keys(process.env)
        .filter((key) => new RegExp(`^${prefix}_`, 'i').test(key))
        .reduce((env, key) => {
            env[key] = process.env[key];
            return env;
        }, {});
    // if(argv.prefix) {
    //   envList['REACT_ENV_PREFIX'] = prefix;
    //   process.env.REACT_ENV_PREFIX = prefix;
    // }
    return envList;
}

function writeBrowserEnvironment(env) {
    const base = realpathSync(process.cwd());
    const path = `${base}/public/__ENV.js`;
    console.debug(`react-env: ${JSON.stringify(env, null, 2)}`);
    const populate = `window.__ENV = ${JSON.stringify(env)};`;
    writeFileSync(path, populate);
    console.info(" Writing runtime env to file: ", path);
}

export async function register() {
    if (process.env.NEXT_RUNTIME === 'nodejs') {
        console.info("Running in Nodejs")
        // await import('./instrumentation-node')
    }

    if (process.env.NEXT_RUNTIME === 'edge') {
        console.info("Running in Edge")
        // await import('./instrumentation-edge')
    }

    writeBrowserEnvironment(env);
}