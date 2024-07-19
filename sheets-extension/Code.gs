const BASE_API_URL = 'https://arrabona-software.solutions';

function FUND_RATE_LOOKUP(isin, attribute = 'rate', startDate = null, endDate = null) {
    var apiUrl = `${BASE_API_URL}/sheets/rates/${isin}`;
    var dataToPost = {};

    if (attribute) {
        dataToPost.attribute = attribute;
    }
    if (startDate) {
        dataToPost.startDate = startDate;
    }
    if (endDate) {
        dataToPost.endDate = endDate;
    }

    var options = {
        'method': 'post',
        'contentType': 'application/json',
        'payload': JSON.stringify(dataToPost)
    };
    var response = UrlFetchApp.fetch(apiUrl, options);

    var responseData = JSON.parse(response.getContentText());

    var reducedData = responseData.map(function(item) {
        var [year, month, day] = item.date.split('/');
        var date = new Date(year, month - 1, day);
        var value = item[attribute];
        if (attribute === "rate") {
            value = parseFloat(value.replace(',', '.'));
        }
        return [date, value];
    });
    return reducedData;
}

function FUND_LOOKUP(manager = null, type = null, category = null, currency = null) {
    var apiUrl = `${BASE_API_URL}/sheets/funds`;
    var dataToPost = {};

    if (manager) {
        dataToPost.manager = manager;
    }
    if (type) {
        dataToPost.type = type;
    }
    if (category) {
        dataToPost.category = category;
    }
    if (currency) {
        dataToPost.currency = currency;
    }

    var options = {
        'method': 'post',
        'contentType': 'application/json',
        'payload': JSON.stringify(dataToPost)
    };
    var response = UrlFetchApp.fetch(apiUrl, options);

    var responseData = JSON.parse(response.getContentText());

    var reducedData = responseData.map(function(item) {
        return [item['isin'], item['manager'], item['type'], item['category'], item['currency']];
    });
    return reducedData;
}