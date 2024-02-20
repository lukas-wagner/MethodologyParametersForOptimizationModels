from sktime.annotation.hmm_learn import PoissonHMM
from sklearn.preprocessing import MinMaxScaler
import pandas as pd
import matplotlib.pyplot as plt

# Import tsd from csv
data = pd.read_csv("tsd_resource1.csv")
# Reformat timestamp columns to datetime
timestamps = data['TimeString']
data['TimeString'] = pd.to_datetime(data['TimeString'], unit='D', origin='1899-12-30')
data.set_index('TimeString', inplace=True)
# remove NaN values
data = data.dropna()
print(data.head())
seq_length = 30000
#seq_length = len(data)
n_cluster = 2
scaler = MinMaxScaler().set_output(transform="pandas")
data_scaled = scaler.fit_transform(data)
model = PoissonHMM(
    n_components=n_cluster,
    algorithm='viterbi',
    tol=0.0001)

model = model.fit(data[:seq_length])
labeled_data = model.predict(data[:seq_length])


# Create a DataFrame with timestamps and labeled data
#result_df = pd.DataFrame({'Timestamp': data.index[:seq_length], 'ClusterLabel': labeled_data})
result_df = pd.DataFrame({'ClusterLabel': labeled_data})
# Save the DataFrame to a CSV file
#result_df.to_csv('system-states-extracted_Poisson_HMM.csv',  index=False)
result_df.to_csv('system-states-extracted_Poisson_HMM_wo_timestamp-dekanter.csv')

# Display the first few rows of the result DataFrame
print(result_df.head())

# Plot data
plt.plot(labeled_data, label='Predicted Labels')
plt.xlabel('Time')
plt.ylabel('Cluster Label')
plt.title('Possion HMM Predicted Labels')
plt.legend()
plt.show()

# Plot data
#plt.plot(data_scaled['Powerel'][:seq_length], color='r', label='Power el normalized')
#plt.xlabel('Time')
#plt.ylabel('Data', color='r')
#plt.title('Data')
#plt.legend()
#plt.show()
