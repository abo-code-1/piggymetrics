# Grafana monitoring

Start the monitoring services from the repository root:

```sh
docker compose -f docker-compose.yml -f docker-compose.grafana.yml up -d grafana
```

Open http://localhost:3000 and sign in with `admin` / `admin`. Grafana asks you
to choose a new password on first login. Open **Dashboards → PiggyMetrics →
PiggyMetrics Overview**. The Prometheus data source and dashboard are provisioned
automatically. Allow about 30 seconds for initial samples.

This command starts only the three monitoring containers. Start the application
using its usual Compose command to see healthy targets. Keep the same Compose
project name so the monitoring services share the application's network.

The dashboard measures gateway HTTP availability and probe duration, TCP
connectivity to services/databases/RabbitMQ, and monitoring scrape health.
TCP connectivity only confirms that a port accepts connections. It does not
verify business operations, database health, or JVM resource usage. Down targets
are expected when the application is stopped. If the exporter itself is down,
probe panels may show no data; check the monitoring scrape health panel.

Grafana listens only on localhost. Prometheus and the probe exporter have no
published host ports. Grafana settings and Prometheus data use named volumes;
Prometheus retains up to seven days or 1 GB of metrics. Do not run Compose
`down -v` unless you intend to delete persistent data.

Set `GRAFANA_PORT` to change the local port, for example:

```sh
GRAFANA_PORT=3001 docker compose -f docker-compose.yml -f docker-compose.grafana.yml up -d grafana
```

## Deployment VM

Copy `docker-compose.grafana.yml` and the entire `observability` directory beside
the deployment Compose file. Use the existing deployment project and env file:

```sh
docker compose --env-file /path/to/piggymetrics/.env -p piggymetrics \
  -f docker-compose.deploy.yml -f docker-compose.grafana.yml up -d grafana
```

Access it through an SSH tunnel, then use http://localhost:3000:

```sh
ssh -N -L 3000:127.0.0.1:3000 user@your-vm
```

The existing deployment workflow does not package this optional monitoring
overlay. Include both the overlay and `observability/` in deployment artifacts
if enabling it through that workflow.

## Configuration and checks

- Probe targets: `observability/prometheus.yml`.
- Probe definitions: `observability/blackbox.yml`.
- Dashboard source: `observability/grafana/dashboards/overview.json`.
- Grafana provisioning: `observability/grafana/provisioning/`.

Restart Prometheus after changing targets. Dashboard edits in Grafana can be
overwritten by provisioning; export them back to the dashboard JSON to keep them.

```sh
docker compose -f docker-compose.yml -f docker-compose.grafana.yml config --quiet
docker compose -f docker-compose.yml -f docker-compose.grafana.yml exec prometheus \
  promtool check config /etc/prometheus/prometheus.yml
curl -fsS http://localhost:3000/api/health
```

Configuration references: [Grafana provisioning](https://grafana.com/docs/grafana/latest/administration/provisioning/)
and [Prometheus multi-target exporters](https://prometheus.io/docs/guides/multi-target-exporter/).
