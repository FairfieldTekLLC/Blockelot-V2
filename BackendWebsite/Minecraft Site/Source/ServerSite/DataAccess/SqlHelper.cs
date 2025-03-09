using System;
using System.Collections.Generic;
using System.Data;
using System.Threading.Tasks;
using Microsoft.Data.SqlClient;
using MySql.Data.MySqlClient;

namespace ServerSite.DataAccess;

public class SqlHelper(DataSourceType dataSourceType)
{
    public DataSourceType DataSourceType { get; set; } = dataSourceType;

    public async Task<bool> ExecuteNonQuery(string connectionString, string sql,
        List<KeyValuePair<string, object>> parameters)
    {
        switch (DataSourceType)
        {
            case DataSourceType.MSQL:
            {
                await using var conn = new SqlConnection(connectionString);
                conn.Open();
                await using var cmd = conn.CreateCommand();
                cmd.CommandText = sql;
                cmd.CommandType = CommandType.Text;
                foreach (var kvp in parameters)
                    cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                await cmd.ExecuteNonQueryAsync();
                break;
            }
            case DataSourceType.MySql:
            {
                await using var conn = new MySqlConnection(connectionString);
                conn.Open();
                await using var cmd = conn.CreateCommand();
                cmd.CommandText = sql;
                cmd.CommandType = CommandType.Text;
                foreach (var kvp in parameters)
                    cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                await cmd.ExecuteNonQueryAsync();
                break;
            }
        }

        return true;
    }

    public async Task<object> ExecuteScalar(string connectionString, string sql,
        List<KeyValuePair<string, object>> parameters)
    {
        switch (DataSourceType)
        {
            case DataSourceType.MSQL:
            {
                await using var conn = new SqlConnection(connectionString);
                conn.Open();
                await using var cmd = conn.CreateCommand();
                cmd.CommandText = sql;
                cmd.CommandType = CommandType.Text;
                foreach (var kvp in parameters)
                    cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                object r = await cmd.ExecuteScalarAsync();
                return r == DBNull.Value ? null : r;
            }
            case DataSourceType.MySql:
            {
                await using var conn = new MySqlConnection(connectionString);
                conn.Open();
                await using var cmd = conn.CreateCommand();
                cmd.CommandText = sql;
                cmd.CommandType = CommandType.Text;
                foreach (var kvp in parameters)
                    cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                object r = await cmd.ExecuteScalarAsync();
                return r == DBNull.Value ? null : r;
            }
            default:
                return null;
        }
    }

    public async Task<List<List<object>>> ExecuteReader(string connectionString, string sql,
        List<KeyValuePair<string, object>> parameters)
    {
        var result = new List<List<object>>();
        switch (DataSourceType)
        {
            case DataSourceType.MSQL:
            {
                await using var conn = new SqlConnection(connectionString);
                conn.Open();
                await using var cmd = conn.CreateCommand();
                cmd.CommandText = sql;
                cmd.CommandType = CommandType.Text;
                foreach (var kvp in parameters)
                    cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                var rdr = await cmd.ExecuteReaderAsync();
                while (rdr.Read())
                {
                    List<object> row = [];
                    for (int i = 0; i < rdr.FieldCount; i++)
                        row.Add(rdr.GetValue(i));
                    result.Add(row);
                }

                return result;
            }

            case DataSourceType.MySql:
            {
                await using var conn = new MySqlConnection(connectionString);
                conn.Open();
                await using var cmd = conn.CreateCommand();
                cmd.CommandText = sql;
                cmd.CommandType = CommandType.Text;
                foreach (var kvp in parameters)
                    cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                var rdr = cmd.ExecuteReader();
                while (rdr.Read())
                {
                    var row = new List<object>();
                    for (int i = 0; i < rdr.FieldCount; i++)
                        row.Add(rdr.GetValue(i));
                    result.Add(row);
                }

                return result;
            }
        }

        return result;
    }
}