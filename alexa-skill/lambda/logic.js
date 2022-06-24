
const axios = require('axios');

module.exports.getCurrent = async function getCurrent() {
    let endpoint = 'http://90.146.147.20:8080';
    let url = endpoint + '/moisture/getPercentage';

    let config = {
        timeout: 6500
    }

    try {
        let response = await axios.get(url, config);
        return  response.data;
    } catch (error) {
        console.log('ERROR', error);
        return null;
    }
}


module.exports.waterPlant = async function waterPlant(duration) {
    let endpoint = 'http://90.146.147.20:8080';
    let url = endpoint + '/waterPlant?duration='+duration;

    let config = {
        timeout: 6500
    }

    try {
        let response = await axios.get(url, config);
        return  response.data;
    } catch (error) {
        console.log('ERROR', error);
        return null;
    }
}



module.exports.getAvg = async function getAvg() {
    let endpoint = 'http://90.146.147.20:8080';
    let url = endpoint + '/moisture/getAvgPercentage';

    let config = {
        timeout: 6500
    }

    try {
        let response = await axios.get(url, config);
        return  response.data;
    } catch (error) {
        console.log('ERROR', error);
        return null;
    }
}