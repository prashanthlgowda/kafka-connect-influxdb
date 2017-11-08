==================
InfluxDB Connector
==================

The InfluxDB Connector provides a Kafka Connect :term:`Source Connector` that can receve data via emulating the
`HTTP API <https://docs.influxdata.com/influxdb/v1.2/guides/writing_data/>`_. A :term:`Sink Connector` is also provided
to write data to an InfluxDB host.

.. toctree::
    :maxdepth: 1
    :caption: Source Connectors:
    :hidden:
    :glob:

    sources/*


.. toctree::
    :maxdepth: 1
    :caption: Sink Connectors:
    :hidden:
    :glob:

    sinks/*


.. toctree::
    :maxdepth: 0
    :caption: Schemas:
    :hidden:

    info
