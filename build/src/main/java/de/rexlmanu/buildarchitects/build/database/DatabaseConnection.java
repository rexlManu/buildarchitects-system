/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2019.
 */
package de.rexlmanu.buildarchitects.build.database;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/******************************************************************************************
 *    Urheberrechtshinweis                                                                *
 *    Copyright © Emmanuel Lampe 2019                                                  *
 *    Erstellt: 09.03.2019 / 20:33                                               *
 *                                                                                        *
 *    Alle Inhalte dieses Quelltextes sind urheberrechtlich geschützt.                    *
 *    Das Urheberrecht liegt, soweit nicht ausdrücklich anders gekennzeichnet,            *
 *    bei Emmanuel Lampe. Alle Rechte vorbehalten.                                        *
 *                                                                                        * 
 *    Jede Art der Vervielfältigung, Verbreitung, Vermietung, Verleihung,                 *
 *    öffentlichen Zugänglichmachung oder andere Nutzung                                  *
 *    bedarf der ausdrücklichen, schriftlichen Zustimmung von Emmanuel Lampe.             *
 ******************************************************************************************/

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class DatabaseConnection {

    @SerializedName("userName")
    private String userName = "root";
    @SerializedName("hostName")
    private String hostName = "localhost";
    @SerializedName("database")
    private String database = "training";
    @SerializedName("password")
    private String password = "";
    @SerializedName("port")
    private int port = 3306;

}
