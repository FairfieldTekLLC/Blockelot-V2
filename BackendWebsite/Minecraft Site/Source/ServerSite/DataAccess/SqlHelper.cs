using System;
using System.Collections.Generic;
using System.Data;
using Microsoft.Data.SqlClient;

namespace ServerSite.DataAccess
{
    public class SqlHelper
    {
        public SqlHelper(DataSourceType dataSourceType)
        {
            DataSourceType = dataSourceType;
        }


        public DataSourceType DataSourceType { get; set; }

        public void ExecuteNonQuery(string connectionString, string sql, List<KeyValuePair<string, object>> parameters)
        {
            if (DataSourceType == DataSourceType.MSQL)
            {
                using (var conn = new SqlConnection(connectionString))
                {
                    conn.Open();
                    using (var cmd = conn.CreateCommand())
                    {
                        cmd.CommandText = sql;
                        cmd.CommandType = CommandType.Text;
                        foreach (var kvp in parameters)
                        {
                            cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                        }

                        cmd.ExecuteNonQuery();
                    }
                }
            }
        }

        public object ExecuteScalar(string connectionString, string sql, List<KeyValuePair<string, object>> parameters)
        {
            if (DataSourceType == DataSourceType.MSQL)
            {
                using (var conn = new SqlConnection(connectionString))
                {
                    conn.Open();
                    using (var cmd = conn.CreateCommand())
                    {
                        cmd.CommandText = sql;
                        cmd.CommandType = CommandType.Text;
                        foreach (var kvp in parameters)
                        {
                            cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                        }

                        var r = cmd.ExecuteScalar();

                        if (r == DBNull.Value)
                            return null;
                        return r;
                    }
                }
            }

            return null;
        }

        public List<List<object>> ExecuteReader(string connectionString, string sql,
            List<KeyValuePair<string, object>> parameters)
        {

            List<List<object>> result = new List<List<object>>();


            using (var conn = new SqlConnection(connectionString))
            {
                conn.Open();
                using (var cmd = conn.CreateCommand())
                {
                    cmd.CommandText = sql;
                    cmd.CommandType = CommandType.Text;
                    foreach (var kvp in parameters)
                    {
                        cmd.Parameters.AddWithValue(kvp.Key, kvp.Value);
                    }

                    var rdr = cmd.ExecuteReader();
                    while (rdr.Read())
                    {
                        List<object> row = new List<object>();
                        for (int i = 0; i < rdr.FieldCount; i++)
                        {
                            row.Add(rdr.GetValue(i));
                        }
                        result.Add(row);
                    }

                    return result;


                }
            }


        }
    }
}
