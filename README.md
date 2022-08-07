# OpenWB PV Charge Calc

This application analyzes log-Files created by OpenWB 1.9 in order to calculate how much energy was charged using PV-energy and how much energy was charged in total.

## Requirements

- openWB 1.9
- Counter for energy imported ("EVU-Counter") installed and configured
- Java >= 8

## Usage
```
java -jar openwb-pv-charge-calc.jar <address>
```
Address can be the IP-Address or hostname of the openWB. The address can optionally be prefixed with a protocol like `http://` or `https://`

Example:
```
java -jar openwb-pv-charge-calc.jar 192.168.0.123 > log.csv
```
Then use your favourite spreadsheet software to analyze the created log.csv. If you just care for the totals you can also use [csvstat from csvkit](https://csvkit.readthedocs.io/en/latest/scripts/csvstat.html):
```
csvstat --sum -c total,pv log.csv
```

## Implementation Notes and Limitations

- The application assumes that charging has the lowest priority when distributing available PV energy to multiple consumers.
- The sums are build by integrating estimated power values from logged counter values. Since one set of values is logged every 5 minutes only, this may introduce quite a margin for errors. Errors coming from this low sampling rate will result in underestimating PV charged energy in some situations.
- Multiple simultaneous charging sessions are not supported and may produce erroneous data.

In general this application is intended to give a "general idea" about how much energy charged is coming from own PV production. Accurracy should generally be regarded as low and results from this application should not be used for accounting purposes or other purposes that require accurate data. 
