const initDataCenter = [
    {
        label: 'DFW',
        value: 'DFW',
    },
    {
        label: 'SJC',
        value: 'SJC',
    },
];

const initCluster = [
    {
        label: 'PRIMARY',
        value: 'PRIMARY',
    },
    {
        label: 'GSB',
        value: 'GSB',
    },
];

const typeData = ['oracle', 'mysql', 'postgres'];

const driverData = {
    oracle: ['oracle.jdbc.driver.OracleDriver'],
    mysql: ['com.mysql.jdbc.Driver'],
    postgres: ['org.postgresql.Driver'],
};

const typeDrivers = [
    {
        value: 'oracle',
        label: 'Oracle',
        children: [
            {
                value: 'oracle.jdbc.driver.OracleDriver',
                label: 'oracle.jdbc.driver.OracleDriver',
            },
        ],
    },
    {
        value: 'mysql',
        label: 'Mysql',
        children: [
            {
                value: 'com.mysql.jdbc.Driver',
                label: 'com.mysql.jdbc.Driver',
            },
        ],
    },
    {
        value: 'postgres',
        label: 'Postgres',
        children: [
            {
                value: 'org.postgresql.Driver',
                label: 'org.postgresql.Driver',
            },
        ],
    },
];

export { initDataCenter, initCluster, typeData, driverData, typeDrivers };
