using System;
using System.Collections.Generic;
using System.Data;
using Microsoft.Data.SqlClient;
using MySql.Data.MySqlClient;

namespace ServerSite.DataAccess
{
    public class SqlHelper(DataSourceType dataSourceType)
    {
        public DataSourceType DataSourceType { get; set; } = dataSourceType;

        public void ExecuteNonQuery(string connectionString, string sql, List<KeyValuePair<string, object>> parameters)
        {
            switch (DataSourceType)
            {
                case DataSourceType.MSQL:
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

                    break;
                }
                case DataSourceType.MySql:
                {
                    using (var conn = new MySqlConnection(connectionString))
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

                    break;
                }
            }
        }

        public object ExecuteScalar(string connectionString, string sql, List<KeyValuePair<string, object>> parameters)
        {
            switch (DataSourceType)
            {
                case DataSourceType.MSQL:
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

                            object r = cmd.ExecuteScalar();

                            return r == DBNull.Value ? null : r;
                        }
                    }
                }
                case DataSourceType.MySql:
                {
                    using (var conn = new MySqlConnection(connectionString))
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

                            object r = cmd.ExecuteScalar();

                            return r == DBNull.Value ? null : r;
                        }
                    }
                }
                default:
                    return null;
            }
        }

        public List<List<object>> ExecuteReader(string connectionString, string sql,
            List<KeyValuePair<string, object>> parameters)
        {

            List<List<object>> result = new List<List<object>>();
            switch (DataSourceType)
            {
                case DataSourceType.MSQL:
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

                            var rdr = cmd.ExecuteReader();
                            while (rdr.Read())
                            {
                                List<object> row = [];
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

                case DataSourceType.MySql:
                {
                    using (var conn = new MySqlConnection(connectionString))
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

            return result;
        }
    }
}
    
