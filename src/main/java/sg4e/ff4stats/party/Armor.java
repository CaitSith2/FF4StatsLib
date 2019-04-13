/*
 * Copyright (C) 2019 sg4e
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sg4e.ff4stats.party;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg4e.ff4stats.RecordParser;

/**
 *
 * @author sg4e
 */
public class Armor implements Equipment {
    
    private final String name, type;
    private final int def, evade, magDef, magEvade;
    private final Stats stats;
    
    private static final String ARMOR_FILE = "equipment/armor.csv";
    private static final Set<Armor> ALL_ARMORS;
    private static final Table<String, String, Armor> ARMOR_TABLE;
    private static final Logger LOG = LoggerFactory.getLogger(Armor.class);
    
    static {
        Set<Armor> armors = new HashSet<>();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(ARMOR_FILE);
        List<CSVRecord> recordList;
        try {
            Reader reader = new InputStreamReader(inputStream);
            recordList = CSVFormat.RFC4180.withHeader().parse(reader).getRecords();
        } catch(IOException ex) {
            LOG.error("Error loading armor stats", ex);
            recordList = new ArrayList<>();
        }
        recordList.forEach(rec -> {
            RecordParser p = new RecordParser(rec);
            Armor a = new Armor(rec.get(0), rec.get(1), p.get(2), p.get(3), p.get(4), p.get(5),
                    new Stats(p.get(6), p.get(7), p.get(8), p.get(9), p.get(10)));
            armors.add(a);
        });
        ALL_ARMORS = Collections.unmodifiableSet(armors);
        
        ImmutableTable.Builder<String, String, Armor> table = new  ImmutableTable.Builder<>();
        ALL_ARMORS.forEach(a -> {
            table.put(a.getName().toLowerCase(), a.getType().toLowerCase(), a);
        });
        ARMOR_TABLE = table.build();
    }

    public Armor(String name, String type, int def, int evade, int magDef, int magEvade, Stats stats) {
        this.name = name;
        this.type = type;
        this.def = def;
        this.evade = evade;
        this.magDef = magDef;
        this.magEvade = magEvade;
        this.stats = stats;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Stats getStats() {
        return stats;
    }

    public int getDefense() {
        return def;
    }

    public int getEvasion() {
        return evade;
    }

    public int getMagicDefense() {
        return magDef;
    }

    public int getMagicEvasion() {
        return magEvade;
    }
    
    public static Armor getArmor(String name, String type) {
        return ARMOR_TABLE.get(name.toLowerCase(), type.toLowerCase());
    }
    
}
