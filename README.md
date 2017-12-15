# Delays API

API to access Deutsche Bahn delays per train/station.

# API

You can access the API under the following URL:

`http://<base-url>/delay/${year}/${month}/${day}/${trainNumber}/${stationEvaNumber}`

Path variables:

* `year`, `month`, `day` - the date
* `trainNumber` - train number, for instance `76` for ICE 76
* `stationEvaNumber` - EVA-number of the station, for instance `8000105` for `Frankfurt(Main)Hbf`

Example:

`http://<base-url>/delay/2017/12/15/592/8000105` - gets the delay of ICE 592 on 15 December 2017 in Frankfurt.

Result:

```
{
	"date": [2017, 12, 15],
	"trainCategory": "ICE",
	"trainNumber": "592",
	"planned": {
		"arrival": [2017, 12, 15, 20, 08]
		"departure": [2017, 12, 15, 20, 14]
	},
	"current": {
		"arrival": [2017, 12, 15, 22, 36]
		"departure": [2017, 12, 15, 22, 40]
	}
}
```
