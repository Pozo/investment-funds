const BASE_API_URL = 'https://arrabona-software.solutions';

function ISIN(isin, attribute, start_date, end_date) {
    var apiUrl = `${BASE_API_URL}/rates/${isin}`;
    var response = UrlFetchApp.fetch(apiUrl);

    var responseData = JSON.parse(response.getContentText());

    var reducedData = responseData.map(function(item) {
        var dateParts = item.date.split('/');
        var date = new Date(dateParts[0], dateParts[1] - 1, dateParts[2]);
        var value = item[attribute];
        if(attribute == "rate") {
            value = parseFloat(value.replace(',', '.'))
        }
        return [date, value];
    });

    Logger.log(reducedData);
    return reducedData;
}