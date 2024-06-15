const BASE_API_URL = 'https://arrabona-software.solutions';

function ISIN(isin, attribute = 'rate', startDate = null, endDate = null) {
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

    Logger.log(reducedData);
    return reducedData;
}